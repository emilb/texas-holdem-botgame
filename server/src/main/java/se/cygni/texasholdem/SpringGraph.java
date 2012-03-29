package se.cygni.texasholdem;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.springframework.aop.framework.Advised;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.Ordered;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

//@Component
public class SpringGraph implements ApplicationContextAware, InitializingBean,
        ResourceLoaderAware, Ordered {

    private final static String NAME_SEPERATOR = "\\" + "n";

    private ApplicationContext ctx;

    private ResourceLoader resourceLoader;

    private String[] includePatterns;

    private String[] excludePatterns;

    private String target;

    public SpringGraph() {

        this.target = "spring-graph.dot";
        this.excludePatterns = new String[] { "org.*" };

        System.out.println("Created graph");
    }

    public void setTarget(final String target) {

        this.target = target;
    }

    public void setIncludePatterns(final String includePatterns) {

        if (StringUtils.hasLength(includePatterns))
            this.includePatterns = includePatterns.split(",");
    }

    public void setExcludePatterns(final String excludePatterns) {

        if (StringUtils.hasLength(excludePatterns))
            this.excludePatterns = excludePatterns.split(",");
    }

    public Graph inspect() {

        final Graph graph = new Graph("spring");

        final Map<Node, Object> beans = new HashMap<Node, Object>();
        for (final String s : ctx.getBeanDefinitionNames()) {

            if (ctx.isSingleton(s) || ctx.isPrototype(s)) {
                Object bean = ctx.getBean(s);

                if (bean instanceof Advised) {
                    try {
                        final Object temp = ((Advised) bean).getTargetSource()
                                .getTarget();
                        if (temp != null)
                            bean = temp;
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
                if (bean == this)
                    continue;
                if (includePatterns != null && includePatterns.length > 0) {
                    boolean matches = false;
                    for (final String pattern : includePatterns)
                        if (matchesWildcard(bean.getClass().getName(), pattern)) {
                            matches = true;
                            break;
                        }
                    if (!matches)
                        continue;
                } else if (excludePatterns != null
                        && excludePatterns.length > 0) {
                    boolean matches = false;
                    for (final String pattern : excludePatterns)
                        if (matchesWildcard(bean.getClass().getName(), pattern)) {
                            matches = true;
                            break;
                        }
                    if (matches)
                        continue;
                }
                beans.put(graph.addNode(s, bean, ctx.isPrototype(s)), bean);
            }
        }
        for (final Map.Entry<Node, Object> entry : beans.entrySet()) {
            final Node node = entry.getKey();
            final Object bean = entry.getValue();
            if (!graph.containsNode(node)) {
                graph.getNodes().add(node);
            }
            Class<?> clazz = bean.getClass();
            do {
                final Field[] fields = clazz.getDeclaredFields();
                for (final Field f : fields) {
                    try {
                        f.setAccessible(true);
                        final Object value = f.get(bean);
                        if (value instanceof Collection) {
                            if (f.getName().equals("beans"))
                                continue;
                            for (final Object obj : (Collection) value) {
                                for (final Map.Entry<Node, Object> en : beans
                                        .entrySet()) {
                                    if (en.getValue() == obj) {
                                        addDependency(graph, node, en.getKey());
                                    }
                                }
                            }

                        } else if (value instanceof Map) {
                            if (f.getName().equals("beans"))
                                continue;
                            for (final Map.Entry var : ((Map<?, ?>) value)
                                    .entrySet()) {
                                for (final Map.Entry<Node, Object> en : beans
                                        .entrySet()) {
                                    if (en.getValue() == var.getKey()
                                            || en.getValue() == var.getValue()) {
                                        addDependency(graph, node, en.getKey());
                                    }
                                }
                            }
                        } else {
                            Node dependency = null;
                            for (final Map.Entry<Node, Object> en : beans
                                    .entrySet()) {
                                if (en.getValue() == value) {
                                    dependency = en.getKey();
                                    break;
                                }
                            }
                            addDependency(graph, node, dependency);
                        }
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
                final Method[] methods = clazz.getDeclaredMethods();

                for (final Method m : methods) {
                    final int mod = m.getModifiers();
                    final Class[] paramTypes = m.getParameterTypes();
                    if (m.getReturnType() == Void.TYPE
                            && Modifier.isPublic(mod)
                            && !Modifier.isStatic(mod)
                            && paramTypes.length == 1
                            && m.getName().startsWith("set")) {
                        for (final Map.Entry<Node, Object> en : beans
                                .entrySet()) {
                            if (paramTypes[0].isAssignableFrom(en.getValue()
                                    .getClass())) {
                                addDependency(graph, node, en.getKey());
                                break;
                            }

                        }

                    }
                }
            } while ((clazz = clazz.getSuperclass()) != null);

        }
        return graph;
    }

    private static String fullName(final String beanName, final Object bean,
            final boolean prototype) {

        final StringBuilder sb = new StringBuilder();
        final Class clazz = bean.getClass();
        final boolean proxy = Proxy.isProxyClass(clazz);
        String className;
        if (proxy) {
            className = clazz.getInterfaces()[0].getName() + NAME_SEPERATOR
                    + "(proxy)";
        } else {
            className = clazz.getName();
        }
        if (beanName.startsWith(className)) {
            sb.append(beanName);
            if (prototype)
                sb.append("(prototype)");
        } else {
            sb.append(beanName);
            sb.append(NAME_SEPERATOR).append(className).toString();
            if (prototype)
                sb.append(NAME_SEPERATOR).append("(prototype)");
        }
        return sb.toString();
    }

    private static boolean matchesWildcard(String text, String pattern) {

        text += '\0';
        pattern += '\0';

        final int N = pattern.length();

        boolean[] states = new boolean[N + 1];
        boolean[] old = new boolean[N + 1];
        old[0] = true;

        for (int i = 0; i < text.length(); i++) {
            final char c = text.charAt(i);
            states = new boolean[N + 1];
            for (int j = 0; j < N; j++) {
                final char p = pattern.charAt(j);

                if (old[j] && (p == '*'))
                    old[j + 1] = true;

                if (old[j] && (p == c))
                    states[j + 1] = true;
                if (old[j] && (p == '?'))
                    states[j + 1] = true;
                if (old[j] && (p == '*'))
                    states[j] = true;
                if (old[j] && (p == '*'))
                    states[j + 1] = true;
            }
            old = states;
        }
        return states[N];
    }

    private static void addDependency(
            final Graph graph,
            final Node node,
            final Node nodeDep) {

        if (nodeDep == null)
            return;

        if (!graph.containsNode(nodeDep)) {
            graph.getNodes().add(nodeDep);
        }
        node.getDependencies().add(nodeDep);
    }

    public static String render(final Graph graph) {

        final StringBuilder sb = new StringBuilder();
        sb.append("digraph ").append(graph.getName()).append(" {\n");
        sb.append("node [shape=egg];\n");

        for (final Node node : graph.getNodes()) {
            sb.append(node.renderStruct()).append("\n\n");
        }

        for (final Node node : graph.getNodes()) {
            // if (node.getDependencies().size() == 0) {
            // sb.append("\"").append(node.getName()).append("\";\n");
            // } else {
            for (final Node n : node.getDependencies())
                sb.append("\"").append(node.getName()).append("\"").append(
                        "->").append("\"").append(n.getName()).append("\"")
                        .append(";\n");
            // }
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if (StringUtils.hasLength(target)) {
            // FileWriter writer = new FileWriter(resourceLoader.getResource(
            // target).getFile());
            final FileWriter writer = new FileWriter(new File(target));
            writer.write(render(inspect()));
            writer.close();
        }
    }

    @Override
    public void setApplicationContext(final ApplicationContext ctx)
            throws BeansException {

        this.ctx = ctx;

    }

    @Override
    public void setResourceLoader(final ResourceLoader resourceLoader) {

        this.resourceLoader = resourceLoader;

    }

    @Override
    public int getOrder() {

        return Ordered.LOWEST_PRECEDENCE;
    }

    public static class Graph {

        private final String name;

        private final Set<Node> nodes = new TreeSet<Node>();

        public Graph(final String name) {

            this.name = name;
        }

        public String getName() {

            return name;
        }

        public Set<Node> getNodes() {

            return nodes;
        }

        public boolean containsNode(final Node node) {

            return nodes.contains(node);
        }

        public Node findNode(final String name) {

            for (final Node node : nodes)
                if (node.getName().equals(name))
                    return node;
            return null;
        }

        public Node addNode(final String beanName, final Object bean,
                final boolean prototype) {

            final Node n = new Node(beanName, bean, prototype);
            getNodes().add(n);
            return n;
        }
    }

    public static class Node implements Comparable<Node> {

        private final String beanName;
        private final String className;
        private final boolean proxy;
        private final boolean prototype;

        private final Set<Node> dependency = new TreeSet<Node>();

        public Node(final String beanName, final Object bean,
                final boolean prototype) {

            this.beanName = beanName;
            this.prototype = prototype;

            final Class clazz = bean.getClass();
            this.proxy = Proxy.isProxyClass(clazz);

            if (proxy) {
                this.className = clazz.getInterfaces()[0].getName();
            } else {
                this.className = clazz.getName();
            }
        }

        public String getName() {

            return beanName;
        }

        public Set<Node> getDependencies() {

            return dependency;
        }

        @Override
        public int compareTo(final Node o) {

            if (beanName == null)
                return -1;
            if (o.getName() == null)
                return 1;
            return beanName.compareTo(o.getName());
        }

        @Override
        public int hashCode() {

            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((beanName == null) ? 0 : beanName.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {

            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final Node other = (Node) obj;
            if (beanName == null) {
                if (other.beanName != null)
                    return false;
            } else if (!beanName.equals(other.beanName))
                return false;
            return true;
        }

        @Override
        public String toString() {

            return beanName;
        }

        public String renderName() {

            final StringBuilder sb = new StringBuilder();
            sb.append(getName());
            if (proxy)
                sb.append(" (proxy)");
            if (prototype)
                sb.append(" (prototype)");
            return sb.toString();
        }

        public String renderStruct() {

            final StringBuilder sb = new StringBuilder();
            sb.append(getName());
            sb.append(" [label=<<TABLE CELLPADDING=\"2\" BORDER=\"0\" CELLSPACING=\"0\">");
            sb.append("<TR><TD>");
            sb.append(renderName());
            sb.append("</TD></TR>");
            sb.append("<TR><TD><FONT POINT-SIZE=\"8.0\" FACE=\"courier-bold\" COLOR=\"grey\">");
            sb.append(renderPackage()).append("</FONT></TD></TR>");
            sb.append("</TABLE>>];");

            return sb.toString();
        }

        public String renderPackage() {

            final String renderedBeanName = renderName();

            if (className.length() < renderedBeanName.length())
                return className;

            final StringBuilder sb = new StringBuilder();
            final StringTokenizer st = new StringTokenizer(className, ".");
            while (st.hasMoreTokens()) {
                int currLen = 0;
                while (currLen < renderedBeanName.length()
                        && st.hasMoreTokens()) {
                    final String next = st.nextToken();
                    currLen += next.length();
                    sb.append(next);
                    if (st.hasMoreTokens())
                        sb.append(".");
                }
                if (st.hasMoreTokens())
                    sb.append("<BR/>");
            }
            return sb.toString();
        }
    }

}
