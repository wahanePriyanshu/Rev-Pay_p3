import os, re
def find_keys():
    for root, _, files in os.walk('.'):
        for f in files:
            if f == 'JwtService.java':
                path = os.path.join(root, f)
                try:
                    with open(path, 'r', encoding='utf-8') as file:
                        content = file.read()
                        match = re.search(r'SECRET_KEY\s*=\s*\"(.*?)\"', content)
                        if match:
                            key = match.group(1)
                            print(f'{path}: {key} (hex: {key.encode().hex()})')
                except Exception as e:
                    print(f'Error reading {path}: {e}')
if __name__ == "__main__":
    find_keys()
