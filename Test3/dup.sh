for f in ./test/*; do
	for i in {1..10}; do 
		cp $f ./test/$f_$i;
	done
done