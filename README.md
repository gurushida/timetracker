**tt** - time tracking utility that keeps track of things with git

What is it ?
------------

This utility allows you to do simple time tracking so that you can know at any time what is your (worked time / expected work time) ratio. By default, it assumes that you work from Monday to Friday, 7.5 hours a day, and that you have 25 days of vacation per year (but you can change the default values). It supports adding public holidays (with the possibility to declare half-days), declaring vacation days, days you stay home because you are sick and days you stay home because you take care of a sick child. In case you wonder how all these days off are even possible, this utility has been designed in a scandinavian country ;-)

You can report worked time by starting and stopping a stopwatch as well as directly declaring time for a given day.

Because time reporting is a matter of trust, this utility versions its internal data with **git** to keep the full history of all the time tracking operations you do once you start tracking. If you do manual modifications on the bookkeeping files of the utility, you will have to commit those changes manually before you can use **tt** again to report worked time or declare holiday, vacation, etc.

Even though this git repository is created as a local one, you are free to push it to a remote location, for instance if you want to do your time tracking across several machines.

Example
-------

```
#!/bin/sh

# This script shows how to use the basic commands.

# Initialize the tracking with a work pattern of 8 hours a day from
# Monday to Friday and 20 days of vacations per year

tt init 01/04/2019 --vacations=20 '--weekpattern=Mon=8:00,Tue=8:00,Wed=8:00,Thu=8:00,Fri=8:00'

# Report only 7 hours on the first day.
# Since this is a script, we need to do it with the add command. In live,
# you may want to use 'tt start' and 'tt stop' instead.

tt add 01/04/2019 7:00

# Work 9 hours on the next day to compensate

tt add 02/04/2019 9:00

# Make the Wednesday a vacation day

tt vacation 03/04/2019

# Make the Thursday a public holiday

tt holiday 04/04/2019 "Feast of No Saints Day for the Church of the Flying Spaghetti"

# Let's work 30 minutes overtime on Friday

tt add 07/04/2019 8:30

# Ooops, that was on Sunday instead of Friday. Let's fix it (with --force because
# we are in a script, but without it the program will ask for confirmation)

tt remove --force worked 07/04/2019 "Fixed mistake"
tt add 05/04/2019 8:30

# Register an extra shift of 4 hours on Saturday

tt plan 06/04/2019 4:00 "Covering for Paul"

# But when working it, you end up working 2 hours overtime

tt add 06/04/2019 6:00 "Dammit Paul..."

# Print report from the start (Monday 01/04/2019) until the given date (Sunday the same week)

tt report all 07/04/2019

# This script will print many things, but here is what the last report command
# will print:

# Monday    01/04/2019:             ############################----
# Tuesday   02/04/2019:             ################################++++
# Wednesday 03/04/2019: vacation    
# Thursday  04/04/2019: holiday     
# Friday    05/04/2019:             ################################++
# Saturday  06/04/2019:             ################++++++++
# Sunday    07/04/2019:             
#
#
# Last week:      30.5h/28.0h worked
# Total overtime: 2.5 hours (from 01/04/2019 to 07/04/2019)

#   ### represents time that was supposed to be worked and that was worked indeed
#   --- represents time that was supposed to be worked and that was not worked
#   +++ represents overtime
#
# Monday and Tuesday have cancelled each other, but there was overtime on Friday
# and Saturday, so we end up with a total 2.5 hours overtime.
```


Installation
------------
This utility is written in Java, so you will need a JDK >= 1.8. It also requires the git command to be in your path.

To build the application jar file, run `./gradlew` on Linux/Mac OS or `gradlew.bat` on Windows.

On Linux or Mac OS, you can then install the `tt` command as well as its man page by running `sudo ./gradlew install`

On Windows, you can run the command with `java -jar timetracker.jar`

User documentation
------------------
See `tt.pdf`, or `man tt` if you are on Linux or Mac OS. 

