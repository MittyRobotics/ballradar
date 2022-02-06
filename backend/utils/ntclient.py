from networktables import NetworkTables

NetworkTables.initialize(server='127.0.0.1')
print("initialized networktables")
table = NetworkTables.getTable('ballradar')
print("found ball radar table")

def add_ball(string):
    table.putString('balldata', string)

def clear():
    table.delete('balldata')