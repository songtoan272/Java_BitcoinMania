# JAVA PROJECT

## Subject : Bitcoin Mania

### Introduction
This project aims to build an app with JAVAFX in order to preview bitcoin value and process to data analysis. It was build in [POO](https://en.wikipedia.org/wiki/Object-oriented_programming).

### Contributors
* [Cao Song Toàn](https://github.com/songtoan272)
* [Damien Smagghe](https://www.damien-smagghe.fr/)
* [Thomas Grandjean](https://github.com/ToMusic75)

### Technologies & tools
* [JAVA 14](https://www.java.com/) for programming
* [JAVAFX 14](https://gluonhq.com/products/javafx/) for GUI
* [GIT](https://git-scm.com/)
* [Github](https://github.com/)
* [MySQL](https://www.mysql.com/)
* [Apache POI](https://poi.apache.org/) for excel reader
* [API Coindesk](https://www.coindesk.com/coindesk-api) for Bitcoin Price

### Setup and run
The application must be started by executing BitcoinApp.jar from the BitcoinApp folder by command line. Try to execute this jar file from any other folder will make the program incapable of reading fxml files. 

To use the login system and SQL export, install MySQL and create a database name bitcoin_mania then change the "username" and "password" to the database in the class MySQLServer

To ease the test process and avoid creating a database to use the application, an alternative system is implemented by reading in all accounts from a CSV file when the application starts. This CSV file will be updated if you register new accounts. You can log into the application by using default account admin/admin or newly registered accounts. All newly registered accounts will be saved in the csv file that is read every time you try to log in. 

NOTICE: This alternative system will only be used if the application cannot reach the MySQL database. If there is a MySQL database configured, this authentification system by CSV file will be bypassed.

The application was successfully tested from command line on Ubuntu 18.04. Please refer to the instruction video if you have problems opening the application.

### Features
Authentication system allowing login and register to the application by using MySQL.
Providing 3 types of chart to represent the price curve of Bitcoin and allow user to save the filtered price points in CSV, PDF or SQL file. 

#### The realtime chart:
- Update the realtime price of Bitcoin from the starting moment of the application
- Price will be update automatically every minute via the API Coindesk
- Allow to choose currency between USD, EUR and GBP 
- Allow to define an upper and a lower threshold. When ever the price pass over (under) the upper (lower) threshold, there will be a popup alert. 
- Informations about each price point and the threshold lines can be easily obtained by hovering the cursor on the data points or on the threshold lines. 
- Each price point is compared to the last available close price.

#### The historical chart:
- Plot the historical daily close prices of Bitcoin since 18/07/2010
- Plot initially the close prices of the last 31 days (today exclueded)
- Allow user to change the graduation between data points, the minumum graduation is one DAY due to the data provided by the API
- Allow user to choose to plot prices between 2 specified dates or by a smart bounds (Last week, Last Month, Last Year)
- Allow user to define an upper and a lower threshold. Hovering over the threshold lines will display the number of data points that pass over or under that threshold.

#### Excel Chart
- Allow user to import an excel file of 2 columns Date and Price and plot all that data.
- Allow user to change the graduation between price points (minimum is 5 mins)
- Allow user to pick period to plot by smart bounds 
- Allow user to pick a period between 2 dates to plot
- Also provide the threshold functionality like in historical chart.

### Difficulties 
- Find an API that provides enough data
- Load too many data rows into Excel chart at once -> crash the program. Go around by limiting the number of data points that can be plotted at once.
- File paths management, unable to locate if not running the application from the right directory
- Export to SQL 