#!/bin/bash

## if you want to trace every command in this script then uncomment below 'set -x' command
#set -x

usage()
{
cat << EOF

cleanup.sh

Clean up all indices in the ElasticSearch node. While cleaning all indices it
will ask user question whether you want to delete all indices or not if
user will say "Yes" or "Y" then only it will delete all indices.

User can list all indices by specifying -l option in this script


USAGE: ./cleanup.sh [OPTIONS]

OPTIONS:
  -h    Show this help message and option
  -a    Deleting all indices in the node
  -l    List all indices in the node
  -u    Elasticsearch NODE URL (default url: http://localhost:9200)
  -o    Output actions to a specified file

EXAMPLES:

  ./cleanup.sh -l

  Connect to http://localhost:9200 and get a list of indices

  ./cleanup.sh -u "http://127.1.1.0:9200" -a -o /var/log/delete_es_indices.log

   Connect to http://127.1.1.0:9200 and get a list of all indices. After
   getting all indices it will delete all of them one by one.
   You have option to specified log file.
   If you have specified log file option then it will write each deleted entry
   in the specified log file.

   NOTE: Deleting all indices is only for testing purpose for developer, if you delete
         all of your indices then you will not able to do search again for already
	 indexed files.

EOF
}

NODE="http://localhost:9200"

delete_all_indices () {

# Store all indices from elasticsearch in ALL_INDICES
ALL_INDICES=$(curl  --silent "$NODE/_cat/indices" | awk '{print $3}')

if [ -z "$ALL_INDICES" ]; then
  echo "There is no indices in  $NODE." >&2
  exit 1
fi

echo "following indices are exist in node: "
echo $ALL_INDICES


## Creating logfile for logging
if [ -n "$LOGFILE" ] && ! [ -e "$LOGFILE" ]; then
  touch "$LOGFILE"
fi

# Deleting all indices
declare -a INDEX=($ALL_INDICES)
for index in ${INDEX[*]} ; do
  if [ -n "$index" ]; then
    if [ -z "$LOGFILE" ]; then
      echo "Deleting index: $index."
      curl -s -XDELETE "$NODE/$index/" > /dev/null
    else
      echo "$(date "+[%Y-%m-%d %H:%M]") Deleting index: $index." >> "$LOGFILE"
      curl -s -XDELETE "$NODE/$index/" >> "$LOGFILE"
    fi
  fi
done
}

## handling all argument options
[ $# -eq 0 ] && usage
while getopts ":hlau:o:" arg; do
  case $arg in
    u)
       echo "in option u"
       NODE=${OPTARG}
       ;;
    l)
       ALL_INDICES=$(curl  --silent "$NODE/_cat/indices" | awk '{print $3}')
       if [ -z "$ALL_INDICES" ]; then
         echo "There is no indices in  $NODE." >&2
         exit 1
       fi
       echo $ALL_INDICES
       exit 0
       ;;
    o)
       echo "in logfile option"
       LOGFILE=${OPTARG}
       ;;
    a)
       echo "Deleting all indices...."
       delete_all_indices
       exit 0
       ;;
    h | *) # Display help.
      usage
      exit 0
      ;;
  esac
done

exit 0
