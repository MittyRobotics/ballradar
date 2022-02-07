from networktables import NetworkTables
import threading

table = None

def waitForConnection():
    cond = threading.Condition()
    notified = [False]

    def connectionListener(connected, info):
        print(info, '; Connected=%s' % connected)
        with cond:
            notified[0] = True
            cond.notify()

    NetworkTables.initialize(server='127.0.0.1')
    NetworkTables.addConnectionListener(connectionListener, immediateNotify=True)

    with cond:
        print("Waiting for NetworkTables Server")
        if not notified[0]:
            cond.wait()
        
    print("Connected To Server!")
    return NetworkTables.getTable("ballradar")

table = waitForConnection()


def check_connected():
    if NetworkTables.isConnected():
        return True
    return False


def add_ball(string):
    table.putString('balldata', string)

def clear():
    table.delete('balldata')