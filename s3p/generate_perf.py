import argparse
import csv
import os
import shutil
import sys
import subprocess


def parse_args():
    parser = argparse.ArgumentParser(description='Prepare CSAR for policy distrition performance test')
    parser.add_argument('--total', required=True, type=int, help='total number of CSAR to be generated')
    parser.add_argument('--dest', required=True, help='dest directory where the CSAR files will be stored')
    parser.add_argument('--out',  default='perf_data.csv', help='list of generated CSAR identifier')
    return parser.parse_args(sys.argv[1:])


def main():
    args = parse_args()
    
    # create dest dir
    shutil.rmtree(args.dest, ignore_errors=True)
    os.makedirs(args.dest)

    # prepartion
    count = 0
    maxwidth = len(str(args.total))
    scripts = os.path.dirname(os.path.abspath(__file__))
    scripts = os.path.join(scripts, 'generate.sh')
    
    with open(args.out, 'w') as out_file:
        out_writer = csv.writer(out_file, delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)
        while (count < args.total):
            seed = str(count).zfill(maxwidth)
            subprocess.check_call([scripts, args.dest, seed])
            out_writer.writerow(["s3p_" + seed])
            count += 1
    return 0

if __name__ == '__main__':
    main()
