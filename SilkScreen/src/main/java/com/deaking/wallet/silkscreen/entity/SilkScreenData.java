package com.deaking.wallet.silkscreen.entity;

import lombok.Builder;
import lombok.Data;

/**
 * Silkscreen 实体类 所有数据都使用小写16进制表示
 * @author shenzucai
 * @time 2018.08.09 18:51
 */
@Data
@Builder
public class SilkScreenData {

    /*1)协议名称       （10字节）    ：Silkscreen
    2)协议版本号     （1字节）     ：例如，01
    3)智能合约地址   （20字节）    ：通用的智能合约地址，数据上链过程中的所有信息都通过该智能合约进行
    4）单位编号       （1字节）     ：例如，03，每个单位对应一个单位编号
    5)数据类型编号   （1字节）     ：每种数据对应一个数据类型编号，编号的首位标识该数据类型是否加密，1表示加密，0表示不加密，例如10000003，表示数据类型要加密，编号为3
    6)产品数据     （≤987字节）   ：用来存放加密后的产品数据 中文字符188个，英文字符566个 utf8编码下
    7)验证位         （1字节）     ：用来验证产品数据是否被篡改*/

    /**
     * 1)协议名称       （10字节）    ：Silkscreen
     */
    private String protocolName = "53696c6b73637265656e";

    /**
     *  2)协议版本号     （1字节）     ：例如，01
     */
    private String protocolVersion = "01";

    /**
     * 3)智能合约地址   （20字节）    ：通用的智能合约地址，数据上链过程中的所有信息都通过该智能合约进行
     */
    private String contractAddress = "ff2847fd9cb24c2babb73ce969a8af163160f395";

    /**
     * 4）单位编号       （1字节）     ：例如，03，每个单位对应一个单位编号
     */
    private String companyNum = "03";

    /**
     * 5)数据类型编号   （1字节）     ：每种数据对应一个数据类型编号，编号的首位标识该数据类型是否加密，1表示加密，0表示不加密，例如10000003，表示数据类型要加密，编号为3
     */
    private String currentDataTypeNum = "83";

    /**
     * 6)产品数据     （≤987字节）   ：用来存放加密后的产品数据 中文字符188个，英文字符566个 utf8编码下
     */
    private String dataOfCompany;

    /**
     * 7)验证位         （1字节）     ：用来验证产品数据是否被篡改
     */
    private String verify;
}
