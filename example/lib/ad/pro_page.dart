/*
 * @Author: lipeng 1162423147@qq.com
 * @Date: 2024-03-12 11:01:20
 * @LastEditors: lipeng 1162423147@qq.com
 * @LastEditTime: 2024-06-02 18:16:00
 * @FilePath: /flutterad/example/lib/ad/pro_page.dart
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import 'package:flutter/material.dart';

/// Pro 页面
class ProPage extends StatefulWidget {
  const ProPage({Key? key}) : super(key: key);

  @override
  State<ProPage> createState() => _ProPageState();
}

class _ProPageState extends State<ProPage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Pro 版付费功能'),
      ),
      body: SingleChildScrollView(
        child: Column(
          children: [
            const Center(
              child: Padding(
                padding: EdgeInsets.all(10.0),
                child: Text(
                  '此功能是插件 Pro 版付费功能，需要购买插件后才能使用\n请联系微信：toponelan 咨询购买并获得技术支持',
                  textAlign: TextAlign.center,
                ),
              ),
            ),
            Image.asset(
              'assets/images/img_pro.png',
              fit: BoxFit.cover,
            ),
            const Center(
              child: Padding(
                padding: EdgeInsets.all(10.0),
                child: Text(
                  '日活 10W 以上，可咨询其他合作模式，目前已有百万级别的客户在合作，获得了非常好的收益提升',
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    color: Colors.purple,
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
