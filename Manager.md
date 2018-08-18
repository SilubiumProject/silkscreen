<a name="getcompanyaddressusingget"></a>
### 根据单位名称获取地址信息
```
GET /manage/company/address
```


#### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Query**|**fromAddress**  <br>*required*|发送地址(管理员)|string|`"SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL"`|
|**Query**|**name**  <br>*required*|单位名称|string||


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[MessageResult](#messageresult)|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


#### Consumes

* `application/json`


#### Produces

* `\*/*`


#### Tags

* 管理员接口操作


<a name="approvecompanyaddressusingget"></a>
### 为单位地址授权
```
GET /manage/company/approve
```


#### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Query**|**address**  <br>*required*|单位地址|string||
|**Query**|**fromAddress**  <br>*required*|发送地址(管理员)|string|`"SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL"`|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[MessageResult](#messageresult)|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


#### Consumes

* `application/json`


#### Produces

* `\*/*`


#### Tags

* 管理员接口操作


<a name="getcompanypublickeyusingget"></a>
### 查询单位公钥
```
GET /manage/company/publickey
```


#### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Query**|**address**  <br>*required*|单位地址|string|`"SLSRN3UeTqRfrFrtS8NtXZgmZYqDLYPzfiC6"`|
|**Query**|**dataType**  <br>*required*|数据类型|string|`"温度"`|
|**Query**|**fromAddress**  <br>*required*|发送地址(管理员)|string|`"SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL"`|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[MessageResult](#messageresult)|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


#### Consumes

* `application/json`


#### Produces

* `\*/*`


#### Tags

* 管理员接口操作


<a name="optiontokenusingget_1"></a>
### 录入单位信息
```
GET /manage/company/save
```


#### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Query**|**address**  <br>*required*|单位地址|string||
|**Query**|**fromAddress**  <br>*required*|发送地址(管理员)|string|`"SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL"`|
|**Query**|**name**  <br>*required*|单位名称|string||


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[MessageResult](#messageresult)|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


#### Consumes

* `application/json`


#### Produces

* `\*/*`


#### Tags

* 管理员接口操作


<a name="setcompanykeyinfousingget"></a>
### 为单位设置加密的秘钥
```
GET /manage/encryptedKey/save
```


#### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Query**|**address**  <br>*required*|单位地址|string|`"SLSRN3UeTqRfrFrtS8NtXZgmZYqDLYPzfiC6"`|
|**Query**|**dataType**  <br>*required*|数据类型|string|`"温度"`|
|**Query**|**encryptedKey**  <br>*required*|经公钥加密的通信秘钥(明文秘钥长度必须小于56字节)|string|`"BPc6QkE3EqceYzE3BsxjyURwj7cfAkXan3UWjkPfcEM3Xp8c5Cc6DZ0nccUT4ns7n6p2kJM1q2TSPuRjXDsCUccqBt5492ILGHgiz9zHstv5NG4clofkZOMhuIYjbFXp0KsQHNxVndXSVxZK/H1fyYbgMJBmR8LBUA=="`|
|**Query**|**fromAddress**  <br>*required*|发送地址(管理员)|string|`"SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL"`|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[MessageResult](#messageresult)|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


#### Consumes

* `application/json`


#### Produces

* `\*/*`


#### Tags

* 管理员接口操作


<a name="getapprovedcompanyaddressesusingget"></a>
### 查询授权公司
```
GET /manage/getApprovedCompanyAddresses
```


#### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Query**|**fromAddress**  <br>*required*|发送地址(管理员)|string|`"SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL"`|
|**Query**|**index**  <br>*required*|单位地址|string|`"0"`|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[MessageResult](#messageresult)|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


#### Consumes

* `application/json`


#### Produces

* `\*/*`


#### Tags

* 管理员接口操作


<a name="getapprovednodeaddressesusingget"></a>
### 查询授权节点
```
GET /manage/getApprovedNodeAddresses
```


#### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Query**|**fromAddress**  <br>*required*|发送地址(管理员)|string|`"SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL"`|
|**Query**|**index**  <br>*required*|节点地址|string|`"0"`|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[MessageResult](#messageresult)|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


#### Consumes

* `application/json`


#### Produces

* `\*/*`


#### Tags

* 管理员接口操作


<a name="getmanagerkeyinfousingget"></a>
### 查询管理员公钥
```
GET /manage/getManagerKeyInfo
```


#### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Query**|**dataType**  <br>*required*|数据类型|string|`"温度"`|
|**Query**|**fromAddress**  <br>*required*|发送地址(管理员)|string|`"SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL"`|
|**Query**|**name**  <br>*required*|公司名称|string|`"佛系"`|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[MessageResult](#messageresult)|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


#### Consumes

* `application/json`


#### Produces

* `\*/*`


#### Tags

* 管理员接口操作


<a name="approvenodeusingget_1"></a>
### 为节点地址授权
```
GET /manage/node/approve
```


#### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Query**|**address**  <br>*required*|节点地址|string|`"SLSVpDD2XZBDvHQFuMvC5i2DSADJmc1zpVw9"`|
|**Query**|**dataType**  <br>*required*|数据类型|string|`"温度"`|
|**Query**|**fromAddress**  <br>*required*|发送地址(管理员)|string|`"SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL"`|
|**Query**|**name**  <br>*required*|公司名称|string|`"佛系"`|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[MessageResult](#messageresult)|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


#### Consumes

* `application/json`


#### Produces

* `\*/*`


#### Tags

* 管理员接口操作


<a name="getnodepublickeyusingget"></a>
### 查询节点公钥
```
GET /manage/node/publickey
```


#### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Query**|**address**  <br>*required*|节点地址|string|`"SLSRN3UeTqRfrFrtS8NtXZgmZYqDLYPzfiC6"`|
|**Query**|**dataType**  <br>*required*|数据类型|string|`"温度"`|
|**Query**|**fromAddress**  <br>*required*|发送地址(管理员)|string|`"SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL"`|
|**Query**|**name**  <br>*required*|公司名称|string|`"佛系"`|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[MessageResult](#messageresult)|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


#### Consumes

* `application/json`


#### Produces

* `\*/*`


#### Tags

* 管理员接口操作


<a name="setnodeapproveinfousingget"></a>
### 录入节点信息
```
GET /manage/node/save
```


#### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Query**|**address**  <br>*required*|节点地址|string|`"SLSVpDD2XZBDvHQFuMvC5i2DSADJmc1zpVw9"`|
|**Query**|**dataType**  <br>*required*|数据类型|string|`"温度"`|
|**Query**|**fromAddress**  <br>*required*|发送地址(管理员)|string|`"SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL"`|
|**Query**|**name**  <br>*required*|公司名称|string|`"佛系"`|
|**Query**|**needCompany**  <br>*required*|是否需要公司授权|string|`"true"`|
|**Query**|**writeAuth**  <br>*required*|是否具有写数据权限|string|`"true"`|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[MessageResult](#messageresult)|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


#### Consumes

* `application/json`


#### Produces

* `\*/*`


#### Tags

* 管理员接口操作


<a name="setnodekeyinfousingget"></a>
### 为节点设置加密的秘钥
```
GET /manage/nodeEncryptedKey/save
```


#### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Query**|**address**  <br>*required*|节点地址|string|`"SLSVpDD2XZBDvHQFuMvC5i2DSADJmc1zpVw9"`|
|**Query**|**dataType**  <br>*required*|数据类型|string|`"温度"`|
|**Query**|**encryptedKey**  <br>*required*|经公钥加密的通信秘钥(明文秘钥长度必须小于56字节)|string|`"BPc6QkE3EqceYzE3BsxjyURwj7cfAkXan3UWjkPfcEM3Xp8c5Cc6DZ0nccUT4ns7n6p2kJM1q2TSPuRjXDsCUccqBt5492ILGHgiz9zHstv5NG4clofkZOMhuIYjbFXp0KsQHNxVndXSVxZK/H1fyYbgMJBmR8LBUA=="`|
|**Query**|**fromAddress**  <br>*required*|发送地址(管理员)|string|`"SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL"`|
|**Query**|**name**  <br>*required*|公司名称|string|`"佛系"`|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[MessageResult](#messageresult)|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


#### Consumes

* `application/json`


#### Produces

* `\*/*`


#### Tags

* 管理员接口操作


<a name="setmanagerkeyinfousingget"></a>
### 管理员录入合作公司数据
```
GET /manage/publickey/save
```


#### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Query**|**dataType**  <br>*required*|数据类型|string|`"温度"`|
|**Query**|**encryptedKey**  <br>*required*|经公钥加密的通信秘钥|string|`"BJxg7IIKxAfWSfnWlqDB5LCcQ7PGoJgsOf5Gss81Nnn5LCOi4Hyk69D35p2nZGWL4m053AIJO2dvoEbOaIsXDMInbjYlbIQtMif4ezOw/iGK+vndQD665SsjYQyYKufZw/2t"`|
|**Query**|**fromAddress**  <br>*required*|发送地址(管理员)|string|`"SLSawiMgzAVwac8T242JdKJ7MqAMMAeH8wpL"`|
|**Query**|**name**  <br>*required*|单位名称|string|`"佛系"`|
|**Query**|**publicKey**  <br>*required*|管理员公钥|string|`"MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEcRdItr0tUEN6XrakIw/GUrO55BzV52RMvb98Gb21lWI8RSdDFRYWH5RElg058rZCnE5/nZi2QWghEswp5znsyw=="`|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[MessageResult](#messageresult)|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


#### Consumes

* `application/json`


#### Produces

* `\*/*`


#### Tags

* 管理员接口操作


<a name="removecompanyusingget"></a>
### 移除授权公司数据类型
```
GET /manage/removeCompany
```


#### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Query**|**address**  <br>*required*|公司地址|string|`"SLSVpDD2XZBDvHQFuMvC5i2DSADJmc1zpVw9"`|
|**Query**|**dataType**  <br>*required*|存入的数据类型(如温度，天气，健康状况等，非程序数据类型)|string|`"温度"`|
|**Query**|**fromAddress**  <br>*required*|发送地址(管理员)|string|`"SLSRN3UeTqRfrFrtS8NtXZgmZYqDLYPzfiC6"`|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[MessageResult](#messageresult)|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


#### Consumes

* `application/json`


#### Produces

* `\*/*`


#### Tags

* 管理员接口操作


<a name="removenodeusingget_1"></a>
### 移除节点数据类型
```
GET /manage/removenode
```


#### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Query**|**address**  <br>*required*|节点地址|string|`"SLSVpDD2XZBDvHQFuMvC5i2DSADJmc1zpVw9"`|
|**Query**|**dataType**  <br>*required*|存入的数据类型(如温度，天气，健康状况等，非程序数据类型)|string|`"温度"`|
|**Query**|**fromAddress**  <br>*required*|发送地址(管理员)|string|`"SLSRN3UeTqRfrFrtS8NtXZgmZYqDLYPzfiC6"`|
|**Query**|**name**  <br>*optional*|公司名称|string|`"佛系"`|


#### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|OK|[MessageResult](#messageresult)|
|**401**|Unauthorized|No Content|
|**403**|Forbidden|No Content|
|**404**|Not Found|No Content|


#### Consumes

* `application/json`


#### Produces

* `\*/*`


#### Tags

* 管理员接口操作
