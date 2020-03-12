import json
import sqlite3

conn = sqlite3.connect('users.db')
c = conn.cursor()

def databaseChecker(badge_id):
    print("received badge id {}".format(badge_id))
    print("checking DB if badge_id {} exists".format(badge_id))
    t = (str(badge_id),)
    c.execute("SELECT username FROM users WHERE badge_id=?",t)
    matches = c.fetchall()
    if (len(matches) > 0) :
        print("User found ! Username is {}\n".format(matches[0][0]))
        return matches[0][0]
    else :
        print("User not found.\n")
        return None

def saveClassEntry(classID,users):
    c.execute("CREATE TABLE IF NOT EXISTS classes (classid VARCHAR(100) UNIQUE, studentsOnEntry TEXT, studentsOnExit TEXT)")
    t = (classID,str(users))
    c.execute("INSERT INTO classes (classid, studentsOnEntry) VALUES (?,?)",t)
    conn.commit()


def saveClassExit(classID,users):
    c.execute("CREATE TABLE IF NOT EXISTS classes (classid VARCHAR(100) UNIQUE, studentsOnEntry TEXT, studentsOnExit TEXT)")
    t = (classID,)
    c.execute("SELECT classid FROM classes WHERE classid=?",t)
    matches = c.fetchall()
    if (len(matches) > 0) :
        t = (str(users),classID)
        c.execute("UPDATE classes SET studentsOnExit = ? WHERE classid = ?",t)
    else :
        t = (classID,str(users))
        c.execute("INSERT INTO classes (classid, studentsOnExit) VALUES (?,?)",t)
    conn.commit()
    

