for f in ../../../../ThanksgivingBingoServer/src/main/sql/*.sql; do
 echo "Exec $f"
 versionhist=`psql -d bingo build -t -c "select count(*) from version_history where filename='$f';"`
 if [ $versionhist -lt 1 ]; then
     psql -d bingo -a -f $f
     psql -d bingo -c "insert into version_history values ('$f', now())"
 fi
done