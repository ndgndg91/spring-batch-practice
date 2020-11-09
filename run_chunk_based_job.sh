CURRENT_DATE=$(date '+%Y/%m/%d')
LESSON=$(basename "$PWD")
./gradlew clean build -x test
java -jar -Dspring.batch.job.names=chunkBasedJob ./build/libs/batch-practice-0.0.1.jar "item=White-Jacket" "run.date(date)=$CURRENT_DATE" "lesson=$LESSON";
read -r;