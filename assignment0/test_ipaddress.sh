#!/bin/bash
#!/bin/bash
PATH_TO_JAVA=.
PATH_TO_TEST_INPUT=tests

echo "These test cases are provided for you convenience only"
echo "Passing these test cases does not imply full marks"
echo -e "++++++++++++++++++++++++++++++++++++++++++++++++++++++\n"

pkill -U $(whoami) java

echo "Testing IPAddress.java"
javac $PATH_TO_JAVA/IPAddress.java
if [ $? -ne 0 ] #check for compile!!
then
  echo -e "\t IPAddress.java didn't COMPILE"
  exit 1
fi
echo -e "\t IPAddress.java COMPLIED without ERROR"

java -cp $PATH_TO_JAVA IPAddress $(cat $PATH_TO_TEST_INPUT/ipaddress.input) 2>&1 > result
diff -wi $PATH_TO_TEST_INPUT/ipaddress.output result 2> /dev/null
if [ $? -ne 0 ]
then
  echo -e "\t IPAddress.java failed"
  exit 1
else
  echo -e "\t Passed test case"
fi

pkill -U $(whoami) java
rm result
