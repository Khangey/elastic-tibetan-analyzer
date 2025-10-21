#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Convert Tibetan Medical QA text file to NDJSON format for Elasticsearch bulk import.
Parses the traditional Tibetan medical knowledge Q&A format.
"""

import json
import os
import sys
import re

def parse_medical_qa(file_path):
    """Parse Tibetan medical QA text file."""
    print(f"Reading medical QA file from: {file_path}")
    
    if not os.path.exists(file_path):
        print(f"Error: File not found at {file_path}")
        sys.exit(1)
    
    qa_pairs = []
    current_question = None
    current_answer_lines = []
    question_number = None
    
    with open(file_path, 'r', encoding='utf-8') as f:
        for line in f:
            line = line.strip()
            
            # Skip empty lines
            if not line:
                # If we have a complete Q&A pair, save it
                if current_question and current_answer_lines:
                    qa_pairs.append({
                        'number': question_number,
                        'question': current_question,
                        'answer': ''.join(current_answer_lines)
                    })
                    current_question = None
                    current_answer_lines = []
                    question_number = None
                continue
            
            # Check if it's a question line (starts with དྲི་བ།)
            if line.startswith('དྲི་བ།'):
                # Save previous Q&A if exists
                if current_question and current_answer_lines:
                    qa_pairs.append({
                        'number': question_number,
                        'question': current_question,
                        'answer': ''.join(current_answer_lines)
                    })
                    current_answer_lines = []
                
                # Extract question number and text
                # Format: དྲི་བ།1. question text
                match = re.match(r'དྲི་བ།(\d+)\.\s*(.+)', line)
                if match:
                    question_number = match.group(1)
                    current_question = match.group(2)
                else:
                    # No number, just text after དྲི་བ།
                    current_question = line.replace('དྲི་བ།', '').strip()
                    
            # Check if it's an answer line (starts with ལན།)
            elif line.startswith('ལན།'):
                # Start collecting answer
                answer_text = line.replace('ལན།', '').strip()
                current_answer_lines.append(answer_text)
                
            # Continuation of answer (no prefix)
            elif current_question and not line.startswith('དྲི་བ།'):
                current_answer_lines.append(line)
    
    # Don't forget the last Q&A pair
    if current_question and current_answer_lines:
        qa_pairs.append({
            'number': question_number,
            'question': current_question,
            'answer': ''.join(current_answer_lines)
        })
    
    return qa_pairs

def convert_to_ndjson(qa_pairs, output_path):
    """Convert QA pairs to NDJSON format."""
    print(f"\nConverting {len(qa_pairs)} QA pairs to NDJSON...")
    
    with open(output_path, 'w', encoding='utf-8') as out:
        for idx, qa in enumerate(qa_pairs, 1):
            # Action line (auto-generate ID)
            action = {"index": {}}
            
            # Document
            doc = {
                "question_number": qa['number'] or str(idx),
                "question": qa['question'],
                "answer": qa['answer'],
                "category": "tibetan_medicine",
                "source": "གསོ་རིག་རྒྱུན་ཤེས།"
            }
            
            out.write(json.dumps(action, ensure_ascii=False) + '\n')
            out.write(json.dumps(doc, ensure_ascii=False) + '\n')
    
    print(f"Conversion completed!")
    print(f"Total QA pairs: {len(qa_pairs)}")
    print(f"Output file: {output_path}")
    
    # Print file size
    size_mb = os.path.getsize(output_path) / (1024 * 1024)
    print(f"File size: {size_mb:.2f} MB")

if __name__ == '__main__':
    # Get script directory
    script_dir = os.path.dirname(os.path.abspath(__file__))
    project_root = os.path.dirname(script_dir)
    
    # Input and output paths
    input_path = os.path.join(project_root, 'test', 'གསོ་རིག་རྒྱུན་ཤེས།.txt')
    output_path = os.path.join(script_dir, 'tibetan-medical-qa-data.json')
    
    # Parse and convert
    qa_pairs = parse_medical_qa(input_path)
    
    if qa_pairs:
        convert_to_ndjson(qa_pairs, output_path)
        
        # Show first 3 examples
        print("\nFirst 3 QA pairs:")
        for i, qa in enumerate(qa_pairs[:3], 1):
            print(f"\n{i}. Q{qa['number']}: {qa['question'][:50]}...")
            print(f"   A: {qa['answer'][:80]}...")
    else:
        print("Warning: No QA pairs found in the file!")
        sys.exit(1)

