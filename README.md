# TTL Fixer
Маленькая программа для фиксации (не путать с изменением!) TTL с возможностью изменения значения.


## Изменение TTL
Для изменения значения необходимо создать файл .fix_ttl (именно так, с точки) со значением TTL в корне /sdcard. На некоторых устройствах по этому адресу находится внутренний накопитель, а на некоторых SD-карта. В случае указания 0, программа выключается. В случае указания неправильного значения, будет ошибка. Значение по умолчанию - 64. Принимаются значения от 0 до 256.