for f in *.png
do
    newName=${f%%.png}
    newName=`echo ${newName/DEUCE__/DEUCE_}`
    
    echo "file: $f to $newName.png"
    mv $f $newName.png
done