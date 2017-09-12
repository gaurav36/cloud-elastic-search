
## python script for performance measurement for ElasticSearch. It will ask user to give directory path, 
## index name, and starting id number.
## It will scan all the files from the directory and index into elastic search incremently starting
## with id number.  
## if user enter null then post request to for indexing files in directory will be skipped.
## For get request it will ask user to enter index name and list of keywords/query/string followed
## by space, it will go through all query string and do get requests one by one for all keywords. 

import os
import json
import urllib2
import requests
import time

def create_index (dir_path):
    index = raw_input("enter index name: ")
    id = input("enter id number: ")
    print "id is: ",id

    
    ## Iterating all file in directory and indexing them
    for root, dirs, files in os.walk(dir_path):
        for file in files:
            p=os.path.join(root,file)
            print "indexing ",p, "with id ", id
            payload = {'filePath': p, 'index': index, 'id': id}
            req = urllib2.Request('http://localhost:8080/springrest/esearch/')
            req.add_header('Content-Type', 'application/json')
            r = urllib2.urlopen(req, json.dumps(payload))

            print r.getcode()
            id += 1
            #time.sleep(1)

print "enter directory name"
get = raw_input()

if get not in ["null", "NULL"]:
    if os.path.exists(get):
        create_index (get);
    else:
        print "enter valid directory name"

#r = requests.get('https://api.github.com/user', auth=('user', 'pass'))
#r = requests.get('http://localhost:8080/springrest/esearch/index1/consensus')
#print(r.status_code, r.reason)
