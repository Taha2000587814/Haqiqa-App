#!/usr/bin/env python3
import re
import os
import sys
from datetime import datetime

def bump_version():
    gradle_path = "app/build.gradle.kts"
    screen_path = "app/src/main/java/com/example/ui/screens/HaqiqaMainScreen.kt"
    readme_path = "build_apks/README.md"

    if not os.path.exists(gradle_path):
        print(f"Error: {gradle_path} not found.", file=sys.stderr)
        sys.exit(1)

    # 1. Read build.gradle.kts
    with open(gradle_path, "r", encoding="utf-8") as f:
        gradle_content = f.read()

    # Find versionCode
    code_match = re.search(r'versionCode\s*=\s*(\d+)', gradle_content)
    if not code_match:
        print("Error: Could not find versionCode in build.gradle.kts", file=sys.stderr)
        sys.exit(1)
    
    old_code = int(code_match.group(1))
    new_code = old_code + 1

    # Find versionName
    name_match = re.search(r'versionName\s*=\s*"([^"]+)"', gradle_content)
    if not name_match:
        print("Error: Could not find versionName in build.gradle.kts", file=sys.stderr)
        sys.exit(1)

    old_name = name_match.group(1)
    
    # Simple semantic version bump for the patch version (e.g., 1.0.3 -> 1.0.4)
    parts = old_name.split('.')
    if len(parts) >= 3:
        try:
            patch = int(parts[-1])
            parts[-1] = str(patch + 1)
            new_name = '.'.join(parts)
        except ValueError:
            new_name = old_name + ".1"
    else:
        new_name = old_name + ".1"

    print(f"Bumping versionCode: {old_code} -> {new_code}")
    print(f"Bumping versionName: {old_name} -> {new_name}")

    # Replace in build.gradle.kts
    updated_gradle = re.sub(
        r'(versionCode\s*=\s*)\d+', 
        f'\\1{new_code}', 
        gradle_content
    )
    updated_gradle = re.sub(
        r'(versionName\s*=\s*)"[^"]+"', 
        f'\\1"{new_name}"', 
        updated_gradle
    )

    with open(gradle_path, "w", encoding="utf-8") as f:
        f.write(updated_gradle)

    # 2. Read and update HaqiqaMainScreen.kt
    if os.path.exists(screen_path):
        with open(screen_path, "r", encoding="utf-8") as f:
            screen_content = f.read()

        # Update the BETA version text
        old_beta_str = f'BETA v{old_name}'
        new_beta_str = f'BETA v{new_name}'
        if old_beta_str in screen_content:
            updated_screen = screen_content.replace(old_beta_str, new_beta_str)
            with open(screen_path, "w", encoding="utf-8") as f:
                f.write(updated_screen)
            print(f"Updated screen display version: {old_beta_str} -> {new_beta_str}")
        else:
            # Fallback regex
            updated_screen, count = re.subn(r'text\s*=\s*"BETA\s+v[^"]+"', f'text = "BETA v{new_name}"', screen_content)
            if count > 0:
                with open(screen_path, "w", encoding="utf-8") as f:
                    f.write(updated_screen)
                print(f"Updated screen display version via regex to BETA v{new_name}")
            else:
                print("Warning: Could not find BETA version string in screen file.", file=sys.stderr)

    # 3. Read and update build_apks/README.md
    if os.path.exists(readme_path):
        with open(readme_path, "r", encoding="utf-8") as f:
            readme_content = f.read()
        
        # Remove "(Current Release)" from older ones
        updated_readme = re.sub(r'### (v\d+\.\d+\.\d+)\s*\(Current Release\)', r'### \1', readme_content)
        
        # Today's date format
        today_str = datetime.now().strftime("%B %d, %Y")
        
        new_release_section = f"""### v{new_name} (Current Release)
- **Release Date**: {today_str}
- **Features**:
  - Automated version bump, compilation, and asset release via GitHub Actions.
- **Artifacts**:
  - `haqiqa-v{new_name}.apk` (Fully compiled debug APK)

"""
        
        # Insert new_release_section right after "## Versioned Releases\n\n"
        target = "## Versioned Releases\n\n"
        if target in updated_readme:
            updated_readme = updated_readme.replace(target, target + new_release_section)
        else:
            updated_readme = updated_readme.replace("## Versioned Releases\n", "## Versioned Releases\n\n" + new_release_section)
            
        with open(readme_path, "w", encoding="utf-8") as f:
            f.write(updated_readme)
        print("Updated build_apks/README.md with new release section.")

    # Output to GitHub Actions environment
    github_output = os.environ.get('GITHUB_OUTPUT')
    if github_output:
        with open(github_output, 'a') as f:
            f.write(f"VERSION_CODE={new_code}\n")
            f.write(f"VERSION_NAME={new_name}\n")
            f.write(f"PREV_VERSION_NAME={old_name}\n")
    
    print("Version bump completed successfully.")

if __name__ == "__main__":
    bump_version()
