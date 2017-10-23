#!/bin/bash
#!/bin/bash
PATH_TO_JAVA=.
PATH_TO_TEST_INPUT=tests

echo "These test cases are provided for you convenience only"
echo "Passing these test cases does not imply full marks"
echo -e "++++++++++++++++++++++++++++++++++++++++++++++++++++++\n"

pkill -U $(whoami) java

echo "Testing Copier.java"
javac $PATH_TO_JAVA/Copier.java
if [ $? -ne 0 ] #check for compile!!
then
  echo -e "\t + Copier.java didn't COMPILE"
  exit 1
fi
echo -e "\t + Copier.java COMPLIED without ERROR"


cp $PATH_TO_TEST_INPUT/checksum.input copier.input
java -cp $PATH_TO_JAVA Copier copier.input result >& /dev/null

diff copier.input result 2> /dev/null
if [ $? -ne 0 ]
then
  echo -e "\t Copier failed"
  exit 1
else
  echo -e "\t Passed test case"
fi

rm copier.input
pkill -U $(whoami) java
rm result


