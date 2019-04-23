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
