# Ian's ScalperBot
(more of a bestbuy stock alert but it's better than nothing)\
Helps you with best buy drops. Multithreaded stock checks. NO AUTOCHECKOUT

# Table of contents

1. [WHAT IT DOES](#what-it-does)
2. [WHAT IT DOESN'T](#what-it-doesnt)
3. [UPDATES???](#will-this-get-updates)
4. [BEFORE YOU START](#before-you-start)
5. [CONFIGURATION](#configuration)
6. [RUNNING](#running)
7. [EDITING](#editing)

## WHAT IT DOES
Helps get you going on best buy drops as soon as they happen

BB has been staggering add to cart links recently so getting into a browser as soon as stock is available is your best chance

Basically, this program checks multiple links at a time using black magic (aka multithreading) and checks for whether or not
stuff is in stock (via web scraping). Once in stock, opens firefox with links to all in-stock items

Also notifies you via discord webhooks and plays a wav file to act as an alarm

## WHAT IT DOESN'T
Auto checkout - I was going to do this but I already got a card so I don't care anymore

Continuous notifications - The bot will stop as soon as it finds what you are looking for

## WILL THIS GET UPDATES???
Probably not

Don't expect me to review pulls on github very often either

## BEFORE YOU START
This program needs a proper jdk\
Heres a few links for where you can get one:
* JDK 15 - https://www.oracle.com/java/technologies/javase-jdk15-downloads.html
* Others - https://www.oracle.com/java/technologies/javase-downloads.html
* Don't go any lower than v11. The jar was compiled for 11+
* Also ps. Some links may need an oracle account. Go for newest version first to avoid this

Also this needs firefox installed on your system.\
You should probably go get that if you want this to work

## CONFIGURATION
If you want an audible alarm - Place a .wav file titled "alarm.wav"\
If you don't want an audible alarm - Delete "alarm.wav"\
If you want discord webhooks:
1. Create a webhook link for your channel. Go look this up if you don't know how
2. Create a webhook.txt file
3. Copy paste the webhook url into the txt file
4. Type in what you want the message to be on the second line of the txt file.\
    EX:\
        \<Insert URL here\>\
        Woah look I found something
5. ??????
6. Profit

Thats it really

## RUNNING
Double click givemecardplz.jar\
A window opens\
It will ask you what you want to search for\
Type this in at the bottom (You should see a text field)\
Press enter\
It will search for products\
A list of options will be listed (If it found something)\
Type out the number of the option you desire\
Press enter\
Wait

Don't worry about missing something. The program WILL let you know (given you did all the setup above)\
Close the window when you're finished

This program is pretty light on resources so you can run multiple instances if you want\
Be careful about your eardrums tho

It also might be a good idea to restart your pc when you're done. Webdriver doesn't exactly have the most graceful of shutdowns, and instances may still be running after you close the program.

Don't mind the source btw if you want to modify that. \
I kinda hacked the rest of this together once I didn't care to finish everything.

## EDITING
This was made in eclipse\
Yes I know pom is probably wrong\
You need to add jars in ./selenium-java-3.141.59 and ./selenium-java-3.141.59/libs to your build path\
You also need to add miglayout15-swing.jar to your build path\
You should be good to go after that
