Multithreaded webcrawler that hunts URLs given a base path.
Database saves and continuing of progress it not supported at the moment.

This is a command-line spring boot application.

The output of the result will be in a temporary file on the system 

Look for the log line "Writing ledger contents to temporary file: ..." C:\Users\...\AppData\Local\Temp\ledger15701664544592926449.txt

Usage ./gradelw bootRun --args="\<targetUrlInHttpsForm\> \<timeoutInSeconds\>" \
Example ./gradlew bootRun --args="https://www.monzo.com 10"
