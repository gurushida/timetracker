#!/bin/sh

# Intialize the tracking with a work pattern of 8 hours a day from
# Monday to Friday and 20 days of vacations per year
tt init 01/04/2019 --vacations=20 '--weekpattern=Mon=8:00,Tue=8:00,Wed=8:00,Thu=8:00,Fri=8:00'

# Report only 7 hours on the first day, then 9 hours on the next one
tt add 01/04/2019 7:00
tt add 02/04/2019 9:00

tt report all 07/04/2019