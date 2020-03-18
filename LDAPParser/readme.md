# LDAP Parser

## Introduction

This python program was intended to parse LDIF Files generated from the CentraleSupelec LDAP. 

This is how it works :

* Copy the sqlite DB `users.db` from the `web/` folder to work on an offline copy
* Import all the info from the file `ldap.ldif` that will have to be in this directory
* Copy back the `users.db` file to the `web/` folder.

## Requirements

This requires the following packages which can all be obtained via pip :
* python-ldap
* sqlite3
* shutil

## Run

run with 

```python3 LdapParser.py```