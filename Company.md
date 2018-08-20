<a name="approvenodeusingget"></a>
### 授权节点
```
GET /company/approvenode
```


#### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Query**|**address**  <br>*required*|节点地址|string|`"SLSVpDD2XZBDvHQFuMvC5i2DSADJmc1zpVw9"`|
|**Query**|**dataType**  <br>*required*|存入的数据类型(如温度，天气，健康状况等，非程序数据类型)|string|`"温度"`|
|**Query**|**fromAddress**  <br>*required*|发送地址(公司)|string|`"SLSRN3UeTqRfrFrtS8NtXZgmZYqDLYPzfiC6"`|
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

* 公司接口操作


<a name="companygetencryptedkeyusingget"></a>
### 查询加密秘钥
```
GET /company/encryptedkey
```


#### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Query**|**dataType**  <br>*required*|存入的数据类型(如温度，天气，健康状况等，非程序数据类型)|string|`"温度"`|
|**Query**|**fromAddress**  <br>*required*|发送地址(公司)|string|`"SLSRN3UeTqRfrFrtS8NtXZgmZYqDLYPzfiC6"`|


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

* 公司接口操作


<a name="companyinputdatainfousingget"></a>
### 录入单位数据信息
```
GET /company/input/datainfo
```


#### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Query**|**dataEncryption**  <br>*required*|是否加密（true,false）|string||
|**Query**|**dataType**  <br>*required*|存入的数据类型(如温度，天气，健康状况等，非程序数据类型)|string|`"温度"`|
|**Query**|**fromAddress**  <br>*required*|发送地址(公司)|string|`"SLSRN3UeTqRfrFrtS8NtXZgmZYqDLYPzfiC6"`|
|**Query**|**publicKey**  <br>*optional*|用于数据加密的公钥|string||


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

* 公司接口操作


<a name="nodegetencryptedkeyusingget"></a>
### 查询节点加密秘钥
```
GET /company/nodeEncryptedkey
```


#### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Query**|**dataType**  <br>*required*|存入的数据类型(如温度，天气，健康状况等，非程序数据类型)|string|`"温度"`|
|**Query**|**fromAddress**  <br>*required*|发送地址(节点)|string|`"SLSVpDD2XZBDvHQFuMvC5i2DSADJmc1zpVw9"`|
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

* 公司接口操作


<a name="nodeinputinfousingget"></a>
### 录入节点数据信息
```
GET /company/nodeinput/datainfo
```


#### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Query**|**dataType**  <br>*required*|存入的数据类型(如温度，天气，健康状况等，非程序数据类型)|string|`"温度"`|
|**Query**|**fromAddress**  <br>*required*|发送地址(节点)|string|`"SLSVpDD2XZBDvHQFuMvC5i2DSADJmc1zpVw9"`|
|**Query**|**name**  <br>*required*|公司名称|string|`"佛系"`|
|**Query**|**publicKey**  <br>*required*|用于数据加密的公钥|string|`"MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEcRdItr0tUEN6XrakIw/GUrO55BzV52RMvb98Gb21lWI8RSdDFRYWH5RElg058rZCnE5/nZi2QWghEswp5znsyw=="`|


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

* 公司接口操作


<a name="removenodeusingget"></a>
### 移除节点数据类型
```
GET /company/removenode
```


#### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Query**|**address**  <br>*required*|节点地址|string|`"SLSVpDD2XZBDvHQFuMvC5i2DSADJmc1zpVw9"`|
|**Query**|**dataType**  <br>*required*|存入的数据类型(如温度，天气，健康状况等，非程序数据类型)|string|`"温度"`|
|**Query**|**fromAddress**  <br>*required*|发送地址(公司)|string|`"SLSRN3UeTqRfrFrtS8NtXZgmZYqDLYPzfiC6"`|
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

* 公司接口操作
