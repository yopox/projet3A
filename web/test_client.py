import json
import asyncio
import websockets
import sqlite3
conn = sqlite3.connect('coucou.db')
c = conn.cursor()

async def testClient():
    uri = "ws://localhost:3001"
    async with websockets.connect(uri) as websocket:
        while True:
            print("awaiting username reception...")
            username = await websocket.recv()
            print("received username id " + username)



print("starting test client...")
asyncio.get_event_loop().run_until_complete(testClient())
asyncio.get_event_loop().run_forever()

