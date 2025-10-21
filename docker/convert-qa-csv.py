#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Convert Tibetan QA CSV data to NDJSON format for Elasticsearch bulk import.
"""

import csv
import json
import os
import sys

def convert_csv_to_ndjson(csv_path, output_path):
    """Convert CSV to NDJSON format."""
    print(f"Reading CSV from: {csv_path}")
    
    if not os.path.exists(csv_path):
        print(f"Error: CSV file not found at {csv_path}")
        sys.exit(1)
    
    count = 0
    with open(csv_path, 'r', encoding='utf-8-sig') as f:
        reader = csv.DictReader(f)
        
        with open(output_path, 'w', encoding='utf-8') as out:
            for row in reader:
                try:
                    # Action line for bulk API (no _id = auto-generate)
                    action = {"index": {}}
                    
                    # Document
                    doc = {
                        "doc_id": row['ID'],
                        "title": row['标题'],
                        "content": row['段落'],
                        "question": row['问题'],
                        "answer": row['答案'],
                        "category": "qa"
                    }
                    
                    # Write action and document (each on separate line)
                    out.write(json.dumps(action, ensure_ascii=False) + '\n')
                    out.write(json.dumps(doc, ensure_ascii=False) + '\n')
                    
                    count += 1
                    
                    if count % 100 == 0:
                        print(f"Processed {count} records...")
                        
                except KeyError as e:
                    print(f"Warning: Missing field {e} in row {row.get('ID', 'unknown')}")
                    continue
    
    print(f"\nConversion completed!")
    print(f"Total records: {count}")
    print(f"Output file: {output_path}")
    
    # Print file size
    size_mb = os.path.getsize(output_path) / (1024 * 1024)
    print(f"File size: {size_mb:.2f} MB")

if __name__ == '__main__':
    # Get script directory
    script_dir = os.path.dirname(os.path.abspath(__file__))
    project_root = os.path.dirname(script_dir)
    
    # Input and output paths
    csv_path = os.path.join(project_root, 'test', '2000_TibetanQA(v2).csv')
    output_path = os.path.join(script_dir, 'tibetan-qa-data.json')
    
    convert_csv_to_ndjson(csv_path, output_path)

