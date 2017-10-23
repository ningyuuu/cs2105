#!/bin/bash
#!/bin/bash
PATH_TO_JAVA=.
PATH_TO_TEST_INPUT=tests

echo "These test cases are provided for you convenience only"
echo "Passing these test cases does not imply full marks"
echo -e "++++++++++++++++++++++++++++++++++++++++++++++++++++++\n"

pkill -U $(whoami) java
rm *.class 2> /dev/null

echo "Testing Checksum.java"
javac $PATH_TO_JAVA/Checksum.java
if [ $? -ne 0 ] #check for compile!!
then
  echo -e "\t Checksum.java didn't COMPILE"
  exit 1
fi

echo -e "\t Checksum.java COMPLIED without ERROR"
cp $PATH_TO_TEST_INPUT/checksum.input checksum.input.tmp
java -cp $PATH_TO_JAVA Checksum checksum.input.tmp 2>&1 > result
rm checksum.input.tmp

diff -wi $PATH_TO_TEST_INPUT/checksum.output result 2> /dev/null
if [ $? -ne 0 ]
then
  echo -e "\t Checksum failed"
  exit 1
else
  echo -e "\t Passed test case"
fi


pkill -U $(whoami) java
rm *.class 2> /dev/null
rm result
