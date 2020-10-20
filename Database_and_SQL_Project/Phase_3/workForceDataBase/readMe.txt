REAM_ME

Things to do or know:
1. You must have a valid oracle account
2. In class TUI.java, on line 21, 4th and 5th parameter, change the user name and password to your user name and password (or just use mine)
3. Must have the referencing database 
4. use trancript.csv for new hire option
5. must use full path to transcript.csv bufferedReader ex /home/user/projects/transcript.csv

compile all with javac -classpath ojdbc8.jar *.java 

run using java -cp :ojdbc8.jar: Runner

Known bugs
1. running the business process of hiring a new person some how corrupts the inputstream yielding all
	scanner objects that pull from the console broken.  The program just ends after the the process is complete


Add tuples cheat sheet 

new course:
00555, Big Data on Parade, advanced, Explore and have lunch with large databases, active, 1230.76, 00511, University of basquaise

new knowledge_skill:
10800, SQL programming, sequel like you've never seen before, intermediate, 00555
10799, Data Models, Data on the cover of vogue, intermediate, 00555

new teaches:
00555, 10800
00555, 10799
00555, 10832

new certificate:
1055, Data Miser, null, University of basquaise

new issues:
00555, 1055

new company:
110011, Dunder Mifflin, Scranton, Slough ave, 1725, suit 200, PA, USA, 18510, Paper, www.dundermifflinpaper.com

new position:
00101, IT guy, null, 31.05, hourly, CS006, 110011

new pos_requires:
10799, 00101, null
10832, 00101, null
10965, 00101, null 

new requires_cert:
00101, 1055

new person:
10011, Rick, James, Townsville, Finch st, 230, Rl 102, OR, USA, 23089, IluvGldFishesCusThySoDelishus@hotmail.com

new works:
10011, 00101, 09/12/2017, null

new has_skill:
10011, 10799
10011, 10832
10011, 10965

new has_cert
10011, 1055


