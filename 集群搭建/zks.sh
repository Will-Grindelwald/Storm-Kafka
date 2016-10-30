#!/bin/bash
version=0.1

echo 
chmod -R 777 init_zks
./init_zks


while :
do 
    clear

    echo 
    echo "***************************************************"
    echo " 自动配置系统  $version - by GQB1226 "
	echo "***************************************************"
	echo ">主菜单"
	echo
	echo " 1 - 配置zookeeper"
	echo " 2 - 配置kafka"
	echo " 3 - 配置storm"
    echo " 4 - 清除所有配置"
	echo " x - 退出"
	echo
	echo -n "请选择:"
    read opt
	case $opt in
		1) scripts/zookeeper;;
		2) scripts/kafka;;
        3) scripts/storm ;;
        4) scripts/del_all ;;
        x) echo "Bye";exit 1;;
		*) echo "非法的输入";continue;;
	esac
	
	echo "Press Enter to continue"
    read enterKey
	
	
done
