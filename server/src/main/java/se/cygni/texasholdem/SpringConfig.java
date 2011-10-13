package se.cygni.texasholdem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import se.cygni.texasholdem.server.GameServer;
import se.cygni.texasholdem.server.GameServiceImpl;
import se.cygni.texasholdem.server.RpcServer;

@Configuration
public class SpringConfig {

    @Autowired
    private GameServiceImpl gameServiceImp;

    @Autowired
    private GameServer gameServer;

    @Autowired
    private SystemSettings systemSettings;

    public @Bean
    RpcServer rpcServer() {

        return new RpcServer(
                systemSettings.getHost(),
                systemSettings.getPort(),
                gameServiceImp, gameServer);
    }
}
