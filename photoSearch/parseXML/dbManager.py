from asyncio.windows_events import NULL
import mysql.connector
from mysql.connector import errorcode

import constant

  #try connection bdd

class DbManager :

    def __init__(self):
        self.cnx=None

    def connect(self,host,database,user,password):
        try:
            self.cnx = mysql.connector.connect(host,database,user,password)        
        except mysql.connector.Error as err:
            if err.errno == errorcode.ER_ACCESS_DENIED_ERROR:
                print("Something is wrong with your user name or password")
            elif err.errno == errorcode.ER_BAD_DB_ERROR:
                print("Database does not exist")
            else:
                print(err)
        else:
            self.cnx.close()

    def disconnect(self):
        if self.cnx != None:
            self.cnx.close()