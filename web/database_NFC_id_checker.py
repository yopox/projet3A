import json
import sqlite3

conn = sqlite3.connect('coucou.db')
c = conn.cursor()

def databaseChecker(badge_id):
    print("received badge id {}".format(badge_id))
    print("checking DB if badge_id {} exists".format(badge_id))
    t = (badge_id,)
    c.execute("SELECT * FROM users WHERE badge_id=?",t)
    matches = c.fetchall()
    if (len(matches) > 0) :
        print("User found ! Username is {}\n".format(matches[0][3]))
        return matches[0][3]
    else :
        print("User not found.\n")
        return None
