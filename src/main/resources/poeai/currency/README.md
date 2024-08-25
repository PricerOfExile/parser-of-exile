# Package dedicated to List the Currencies/Fragments which can be use to price an item.

## currencies.json

```shell
curl 'https://poe.ninja/api/data/currencyoverview?league=Necropolis&type=Currency' --compressed \
-H 'Accept: */*' \
-H 'Accept-Language: en-US,en;q=0.5' \
-H 'Accept-Encoding: gzip, deflate, br, zstd' \
-H 'Referer: https://poe.ninja/economy/necropolis/currency' \
-H 'Connection: keep-alive'
```

## fragments.json

```shell
curl 'https://poe.ninja/api/data/currencyoverview?league=Necropolis&type=Fragment' --compressed \
-H 'Accept: */*' \
-H 'Accept-Language: en-US,en;q=0.5' \
-H 'Accept-Encoding: gzip, deflate, br, zstd' \
-H 'Referer: https://poe.ninja/economy/necropolis/currency' \
-H 'Connection: keep-alive'
```
