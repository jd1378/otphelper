import os
import xml.etree.ElementTree as ET

def get_strings(file_path):
    tree = ET.parse(file_path)
    root = tree.getroot()
    strings = {child.attrib['name']: child for child in root.findall('string')}
    return strings, tree, root

def update_strings(default_strings, target_strings, target_root):
    for key, element in default_strings.items():
        if key not in target_strings:
            translatable = element.attrib.get('translatable', 'true')
            if translatable.lower() == 'true':
                new_string = ET.Element('string', name=key)
                new_string.text = element.text
                target_root.append(new_string)
                print(f"Added missing translatable string '{key}'")
            else:
                print(f"Skipped non-translatable string '{key}'")

def prettify(element, indent="    "):
    queue = [(0, element)]  # (level, element)
    while queue:
        level, element = queue.pop(0)
        children = [(level + 1, child) for child in list(element)]
        if children:
            element.text = "\n" + indent * (level + 1)  # for child open
        if queue:
            element.tail = "\n" + indent * queue[0][0]  # for sibling open
        else:
            element.tail = "\n" + indent * (level - 1)  # for parent close
        queue[0:0] = children

def save_xml(tree, file_path):
    prettify(tree.getroot()) 
    xml_declaration = '<?xml version="1.0" encoding="utf-8"?>\n'
    xml_content = ET.tostring(tree.getroot(), encoding="unicode")
    xml_content = xml_declaration + xml_content
    xml_content = xml_content.replace('\r\n', '\n') # LF ending normalization
    with open(file_path, 'wb') as f:
        f.write(xml_content.encode('utf-8'))
    print(f"Updated strings file saved at {file_path}")

def main():
    root_dir = os.path.dirname(os.path.realpath(__file__))
    res_dir = os.path.join(root_dir, 'app', 'src', 'main','res')
    default_file = os.path.join(res_dir, 'values', 'strings.xml')

    if not os.path.isfile(default_file):
        print(f"Default strings file not found at {default_file}")
        return

    default_strings, default_tree, default_root = get_strings(default_file)

    for lang_dir in os.listdir(res_dir):
        if lang_dir.startswith('values-'):
            lang_file = os.path.join(res_dir, lang_dir, 'strings.xml')
            if os.path.isfile(lang_file):
                target_strings, target_tree, target_root = get_strings(lang_file)
                update_strings(default_strings, target_strings, target_root)
                save_xml(target_tree, lang_file)
            else:
                print(f"No strings.xml found for {lang_dir}")

if __name__ == "__main__":
    main()