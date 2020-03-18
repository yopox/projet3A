# -*- coding: utf-8 -*-
import sys
import subprocess
from ldif import LDIFParser,LDIFWriter
import sqlite3
import shutil

SKIP_DN = ["ou=people,dc=centralesupelec,dc=fr"]
ERRORS = {}
ERRORS['cnErrors'] = []
ERRORS['affiliationErrors'] = []
ERRORS['loginError'] = []
ERRORS['firstLastNameError'] = []
ERRORS['mailErrors'] = []
ERRORS['badgeErrors'] = []
ERRORS['promotionErrors'] = []
ERRORS['nedapProfileErrors'] = []
ERRORS['supannDiplomeErrors'] = []



class SQLUsersTableWriter():
  def __init__(self, db):
    self.db = db
    self.id = 0
    print("Droping old table...")
    db.execute("DROP TABLE IF EXISTS users")
    print("Creating new table...")
    db.execute("""CREATE TABLE users (
      id INT PRIMARY KEY NOT NULL,
      cn VARCHAR(100),
      lastname VARCHAR(100),
      firstname VARCHAR(100),
      username VARCHAR(100) UNIQUE NOT NULL,
      email VARCHAR(255),
      badge_id VARCHAR(64),
      skowa_id VARCHAR(64),
      affiliations VARCHAR(255),
      primary_affiliation VARCHAR(255),
      nedap_profile VARCHAR(255),
      supann_diplome VARCHAR(255),
      promotion VARCHAR(255)
    )""")
    print("Table created successfully")
  
  # To be called by the LDIF handler at each round. Id needs to be incremented each time to ensure unicity
  def writerow(self, row):
    self.id += 1
    request = """INSERT INTO users (
      id,
      cn,
      lastname,
      firstname,
      username,
      email,
      badge_id,
      skowa_id,
      affiliations,
      primary_affiliation,
      nedap_profile,
      supann_diplome,
      promotion)
    VALUES (""" + str(self.id) + ',"' + '","'.join([str(x) for x in row]) + '")'
    db.execute(request)
    db.commit()
    return

class MyLDIF(LDIFParser):
  def __init__(self,input,database_writer):
    LDIFParser.__init__(self,input)
    self.writer = database_writer
  def handle(self,dn,entry):
    if dn in SKIP_DN:
      return
    # CN
    try:
      cn = [x.decode("utf-8") for x in entry['cn']]
    except:
      ERRORS['cnErrors'].append(str(dn))
      return
    # First and Last Names
    try:
      prenom = [x.decode("utf-8") for x in entry['centralesupelecPrenomsEtatCivil']]
      nom = [x.decode("utf-8") for x in entry['centralesupelecNomEtatCivil']]
    except:
      prenom = ""
      nom = ""
      ERRORS['firstLastNameError'].append(str(dn))
    # There are 2 email fields in the CS Ldap... Let's check both 
    try:
      mail = [x.decode("utf-8") for x in entry['mail']]
    except:
      try:
        mail = [x.decode("utf-8") for x in entry['centralesupelecStudentMail']]
      except:
        mail = ""
        ERRORS['mailErrors'].append(str(dn))
    # Affiliations (to separate students & employees)
    try:
      affiliation = [x.decode("utf-8") for x in entry['eduPersonAffiliation']]
      primaryAffiliation = [x.decode("utf-8") for x in entry['eduPersonPrimaryAffiliation']]
    except :
      ERRORS['affiliationErrors'].append(str(dn))
      affiliation = ""
      primaryAffiliation = ""
    # Login
    try:
      login = [x.decode("utf-8") for x in entry['supannAliasLogin']]
    except:
      ERRORS['loginError'].append(str(dn))
      login = ""
    # Badge NFC & Physical ID
    try:
      badgeNFCID = [x.decode("utf-8") for x in entry['centralesupelecBadgeSN']]
      badgePhysicalID = [x.decode("utf-8") for x in entry['centralesupelecBadgeZdS']]
    except:
      ERRORS['badgeErrors'].append(str(dn))
      badgeNFCID = ""
      badgePhysicalID = ""
    # Where in the promotion ?
    try:
      promotionID = [x.decode("utf-8") for x in entry['supannEtuEtape']]
    except:
      ERRORS['promotionErrors'].append(str(dn))
      promotionID = ""
    # Nedap profile
    try:
      nedapProfile = [x.decode("utf-8") for x in entry['centralesupelecNedapProfile']]
    except:
      ERRORS['nedapProfileErrors'].append(str(dn))
      nedapProfile = ""
    # Supann Diplome
    try:
      supannDiplome = [x.decode("utf-8") for x in entry['supannEtuDiplome']]
    except:
      ERRORS['supannDiplomeErrors'].append(str(dn))
      supannDiplome = ""
    # Most of these fields were lists, but with only one member. Let's clean that up ! 
    toWrite = [cn,nom,prenom,login,mail,badgeNFCID,badgePhysicalID,affiliation,primaryAffiliation,nedapProfile,supannDiplome]
    for id,x in enumerate(toWrite) :
      if len(x) == 1:
        toWrite[id] = x[0]
    toWrite.append(promotionID)
    self.writer.writerow(toWrite)

with open('ldap.ldif','rb') as infile:
  print("Copying DB file...")
  shutil.copy2("../web/users.db",".")
  print("opening sql table")
  db = sqlite3.connect('users.db')
  print("Opened database successfully")

  database_writer = SQLUsersTableWriter(db)
  print("Starting LDIF File parsing....")
  MyLDIF(infile,database_writer).parse()

  print("Done !")
  
  print("There were some Errors while unparsing the LDIF File: \n")
  print("\ncn Errors : " + str(ERRORS['cnErrors']))
  print("\nAffiliation Errors : " + str(ERRORS['affiliationErrors']))
  print("\nLogin Errors : " + str(ERRORS['loginError']))
  print("\nName Errors : " + str(ERRORS['firstLastNameError']))
  print("\nMail Errors : " + str(len(ERRORS['mailErrors'])) + " errors")
  print("\nBadge Errors : " + str(len(ERRORS['badgeErrors'])) + " errors")
  
  infile.close
  db.close()

  print("Copying DB file...")
  shutil.copy2("users.db","../web/")
  print("File copied")
  print("synchronization with LDAP complete !")