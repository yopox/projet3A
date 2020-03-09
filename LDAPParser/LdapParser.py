# -*- coding: utf-8 -*-
import sys
import unicodecsv as csv
import subprocess
import csv_to_sqlite
from ldif import LDIFParser,LDIFWriter
import sqlite3

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


class MyLDIF(LDIFParser):
  def __init__(self,input,csv_writer):
    LDIFParser.__init__(self,input)
    self.writer = csv_writer
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
    # Most of these fields were lists, but with only one member. Let's clean that up ! 
    toWrite = [cn,login,prenom,nom,affiliation,primaryAffiliation,mail,badgeNFCID,badgePhysicalID,nedapProfile]
    for id,x in enumerate(toWrite) :
      if len(x) == 1:
        toWrite[id] = x[0]
    toWrite.append(promotionID)
    self.writer.writerow(toWrite)


with open('ldap.ldif','rb') as infile, open('LDIFExtract.csv','wb') as outfile:
  print("Starting LDIF File parsing....")
  csv_writer = csv.writer(outfile)
  csv_writer.writerow(['cn','username','firstname','lastname','Affiliation','Primary Affiliation','email','badge_id','badge_physical_id','nedap_profile','promotion'])
  MyLDIF(infile,csv_writer).parse()
  print("There were some Errors : \n")
  print("\ncn Errors : " + str(ERRORS['cnErrors']))
  print("\nAffiliation Errors : " + str(ERRORS['affiliationErrors']))
  print("\nLogin Errors : " + str(ERRORS['loginError']))
  print("\nName Errors : " + str(ERRORS['firstLastNameError']))
  # print("\nMail Errors : " + str(ERRORS['mailErrors']))
  # print("\nBadge Errors : " + str(ERRORS['badgeErrors']))
  infile.close
  outfile.close

##subprocess.call("python3 csv-to-sqlite -f LDIFExtract.csv -o coucou.db")