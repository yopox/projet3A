import nxppy
import json
import time
import sys
import asyncio
import websockets
import database_utils


mifare = nxppy.Mifare()

async def nfcPoll():
    try:
        uid = mifare.select()
        print("found badge !")
        if (len(uid)%2 != 0 ):
            uid = str(0) + uid
        new_uid = ""
        for i in range(len(uid)//2) :
            new_uid = uid[int(2*i)] + uid[int(2*i+1)] + new_uid
        uid = new_uid
        badge = {}
        user = {}
        badge["id"] = uid
        user["username"] = database_utils.databaseChecker(badge["id"])
        user["badgeid"] = badge["id"]
        return json.dumps(user)
    except nxppy.SelectError:
        # SelectError is raised if no card is in the field.
        await asyncio.sleep(.7)
        return None
        


async def checkOnce():
    return await nfcPoll()

async def checkClass(websocket):
    users_list = []
    # ID will be used to regularly check that the connection is still up
    id = 1
    try:
        while True:
            user = await nfcPoll()
            if not user:
                # Every 10 fails, we just check that the connection is still up
                if id == 5:
                    # If it is not up, this will fail and go to the finally statement
                    print("checking if socket connection is up...")
                    await websocket.send("")
                    print("connection still up, continuing")
                    id = 0
                id += 1
                continue
            if (user not in users_list):
                users_list.append(user)
            await websocket.send(user)
            await asyncio.sleep(.7)
    finally:
        print("Connection is down. Saving class")
        return users_list

async def handler(websocket, path):
    # Awaiting connection type
    # 0: Once
    # 1: Class Entrance verification
    # 2: Class Exit Verification
    print("starting server...")
    type_of_connection = await websocket.recv()
    type_of_connection = json.loads(type_of_connection)

    # Once 
    if type_of_connection["type"] == 0:
        print("Type of connection : once")
        classID = None
        user = await checkOnce()
        await websocket.send(json.dumps(user))

    # Class Entrance and Exit
    else :
        classID = type_of_connection["id"]
        users = await checkClass(websocket)


        if (type_of_connection["type"] == 1):
            print("Type of connection : class entry")
            database_utils.saveClassEntry(classID,users)
            
        if (type_of_connection["type"] == 2):
            print("Type of connection : class exit")
            database_utils.saveClassExit(classID,users)
        
    print("Closing Connection...")



start_server = websockets.serve(handler, "", 3001)

print("starting fake NFC server...")
asyncio.get_event_loop().run_until_complete(start_server)
asyncio.get_event_loop().run_forever()