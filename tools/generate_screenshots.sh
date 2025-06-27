#!/usr/bin/env bash

# Using this files requires you to install "pngquant" and "oxipng"

export MSYS_NO_PATHCONV=1

function clear_status_bar {
    # set date to June 10 2025 at 11 oclock
    adb shell date 010611002025

    # Start demo mode
    adb shell settings put global sysui_demo_allowed 1

    # Display time 11:00
    adb shell am broadcast -a com.android.systemui.demo -e command clock -e hhmm 1100
    # Display full mobile data without type
    adb shell am broadcast -a com.android.systemui.demo -e command network -e mobile show -e level 4 -e datatype false
    adb shell am broadcast -a com.android.systemui.demo -e command network -e wifi show -e level 4 -e fully true
    # Hide notifications
    adb shell am broadcast -a com.android.systemui.demo -e command notifications -e visible false
    # Show full battery but not in charging state
    adb shell am broadcast -a com.android.systemui.demo -e command battery -e plugged false -e level 100
    clear_notifications
}

function reset_status_bar {
    adb shell am broadcast -a com.android.systemui.demo -e command exit
}

function navigate {
    adb shell am start -a android.intent.action.VIEW -d "otphelper://$1"
    adb shell am start -a android.intent.action.VIEW -d "otphelper://$1" # because once may not work
}

function restart_app {
    adb shell am broadcast -a io.github.jd1378.otphelper -e command restart
}

function change_lang {
    adb shell cmd locale set-app-locales io.github.jd1378.otphelper --user current --locales $1
    sleep 1.5
}

function reset_lang {
    adb shell am set-app-locales --package io.github.jd1378.otphelper --locales ""
    sleep 1.5
}

function tap {
    adb shell input tap $1 $2
}

function swipe {
    # input swipe <x1> <y1> <x2> <y2> [duration(ms)]
    adb shell input tap $1 $2 $3 $4 20
}

function expand_status_bar {
    adb shell service call statusbar 1
}

function collapse_status_bar {
    adb shell service call statusbar 2
}

function clear_notifications {
  num=$(adb shell dumpsys notification | grep NotificationRecord | wc -l)
  if [ $num -gt 0 ]; then
    expand_status_bar
    sleep 1
    while [ $num -gt 0 ]; do
      adb shell input swipe 100 720 300 720 50
      sleep 0.5
      num=$(( $num - 1 ))
    done
    collapse_status_bar
    sleep 1
  fi
}

function goto_home {
    adb shell input keyevent KEYCODE_HOME
}

function goto_app {
    adb shell am start io.github.jd1378.otphelper/.MainActivity
}

function enable_dark_mode {
   adb shell "cmd uimode night yes"
}

function disable_dark_mode {
   adb shell "cmd uimode night no"
}

function save_screenshot {
    adb exec-out screencap -p > ./screen-tmp.png

    pngquant --strip --skip-if-larger --force --quality 85-99 ./screen-tmp.png -o ./screen-tmp.png
    oxipng --strip safe ./screen-tmp.png --out ./screen-tmp.png
    
    mv ./screen-tmp.png "$1"
}

function select_adb_device {
  devices=($(adb devices | awk 'NR>1 && $2=="device" {print $1}'))
  count=${#devices[@]}
  if [ $count -eq 0 ]; then
    echo "No adb devices found."
    exit 1
  elif [ $count -eq 1 ]; then
    export ANDROID_SERIAL="${devices[0]}"
    echo "Using device: ${devices[0]}"
  else
    echo "Multiple adb devices found:"
    for i in "${!devices[@]}"; do
      echo "$((i+1)). ${devices[$i]}"
    done
    read -p "Select device [1-$count]: " idx
    idx=$((idx-1))
    if [ $idx -ge 0 ] && [ $idx -lt $count ]; then
      export ANDROID_SERIAL="${devices[$idx]}"
      echo "Using device: ${devices[$idx]}"
    else
      echo "Invalid selection."
      exit 1
    fi
  fi
}

select_adb_device

# cleanup from before

rm -f ./screen-tmp.png

locales=('en-US' 'ar' 'bn-BD' 'de' 'es' 'fa' 'fr' 'it' 'ko' 'tr' 'ru' 'tr' 'uk' 'vi' 'zh-Hans' 'zh-Hant')

clear_status_bar

# set timezone to london
adb shell service call alarm 3 s16 Europe/London

goto_app

for i in "${locales[@]}"
do
    clear_notifications
    adb shell date 010611002025

    if [ "$1" == 'tablet' ]; then
        scrDir="./tablet/$i/"
    else
        scrDir="../metadata/android/$i/images/phoneScreenshots"
    fi
    
    mkdir -p $scrDir

    navigate home
    change_lang $i
    sleep 1.5 # wait for broadcast to finish
    save_screenshot "$scrDir/1.png"

    # tap on send test notification
    tap 540 1560
    sleep 0.3
    expand_status_bar
    sleep 1.5
    save_screenshot "$scrDir/3.png"
    collapse_status_bar
    sleep 1

    navigate settings
    sleep 1
    save_screenshot "$scrDir/2.png"
done

stop_clean_status_bar
