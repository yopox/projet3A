# web app

## Introduction

This is the web app associated with the project. It holds for now the NFC verification, but is not yet associated with the SKoWA Verifications. 

## How it works

The NFC part is managed by the `poll_nfc_badge.py` file which communicated with `database_utils.py` to save and access the db. 

It then uses a websocket to transfer information to the frontend of the Express JS application. 

This application is used to start and stop classes, and do the necessary saves. 

## Project structure

This app is separated in 3 pages: 

### Users

This page lists all users from the `users.db` sqlite3 database.

You can access it via `http://<URL>/users/<number>/<offset>` where: 
* `number` is the number of users to display on the page
* `offset` is the offset in the database. 

By default, you can access `http://<URL>/users` which will put 25 users on the page with an offset of 0.

### Class

This will currently list all the `SIS` students. Bases for filtering have been implemented, but are not currently functionnal. Unfortunately, the CS Ldap being what it is, it is not easy for the user to filter out the right parameters, which is the reason why it is not yet functionnal.

The verification can be controlled with the 4 buttons on the top which will manage the server. 

When a user is badged, the corresponding boxes will be ticked (`Entered` and `Exited`).

### Presence

After a class is done, this page will recapitulate the presence, at entry and at exit. For now, the `classID` is a timestamp of the time at which the `Start Entry Verification` button is hit for a class. It is determined on the class page.

## Requirements

### Node JS

This is an express js application. Just run `npm install` in this directory 

### Bootstrap material design icons

After running `npm install`, you may need to run the following command from the root of the projectunder a linux terminal to make sure that a simlink is created to the public folder, which holds all the static stuff :

```ln -sr web/node_modules/bootstrap-material-design-icons web/public```

### Python

You will need the following packages which can be installed via pip :
* nxppy
* asyncio
* websockets
* json
* sqlite3