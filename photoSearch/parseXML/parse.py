from xml.dom import minidom
import constant
import dbManager

myDb = dbManager.DbManager()
print("cnx =",myDb.cnx)
myDb.connect(constant.dbUser, constant.dbPassword, constant.dbHost, constant.dbName)

  # parse an xml file
file = minidom.parse('gt_import_stock1600_formated.xml')
