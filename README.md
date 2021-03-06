## "# silkscreen"

# Silubium公链 数据上链协议【SILKSCREEN】 已设计完成，正在测试和申请专利，敬请期待...


### 企业数据上链流程
适用于使用silkscreen协议上链数据的企业。silkscreen使用主控智能合约进行数据的安全上链，参见：![](https://slugithub.oss-cn-beijing.aliyuncs.com/silkscreen/silkscreen-V5.jpg)

#### 一、准备阶段
1. 企业准备一个SLU地址接收数据
SLU地址可以通过[全节点钱包](http://update.silubium.org)或[网页钱包](https://webwallet.silubium.org)获得。
2. [迪肯公司](http://www.deaking.net)在智能合约中为企业授权（单位名称和第1步获得的地址）、提供接口调用方法
3. 企业在智能合约中设置上链数据类型、是否加密、以及用于加密公钥
4. 迪肯公司生成数据加密密钥，并以对应数据类型的公钥加密后存入智能合约

#### 二、数据上链
1. 企业通过智能合约取出对应数据类型的密钥密文，用私钥进行解密，得到数据加密密钥
2. 调用迪肯公司提供的接口方法，用加密密钥对需上链数据进行加密，并将数据发送到SILUBIUM公链上，发送成功后，可以根据交易ID在[区块链浏览器]( https://silkchain2.silubium.org)上查询

#### 三、数据查询
1. 调用迪肯公司提供的接口方法，取出链上数据
2. 第三方公司如需使用链上数据，需要迪肯公司和企业共同授权后才可。

#### 四、接口方法说明
1. 自行搭建内部服务器，访问网址为：http://127.0.0.1:7020 
2. 内部服务器搭建完成后，访问API接口网址为:http://127.0.0.1:7020/index.html 该API使用[Swagger UI](https://swagger.io/tools/swagger-ui/)进行接口调试
3. [接口详情](api.md)
4. [接口返回值](result.md)

