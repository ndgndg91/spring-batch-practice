CURRENT_DATE=$(date '+%Y/%m/%d')
LESSON=$(basename "$PWD")
./gradlew clean build -x test
java -jar -Dspring.batch.job.names=writeJsonFileJob ./build/libs/batch-practice-0.0.1.jar "run.date(date)=$CURRENT_DATE" "lesson=$LESSON";
read -r;