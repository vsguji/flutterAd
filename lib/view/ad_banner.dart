/*
 * @Author: lipeng 1162423147@qq.com
 * @Date: 2024-06-01 21:27:58
 * @LastEditors: lipeng 1162423147@qq.com
 * @LastEditTime: 2024-06-03 14:22:58
 * @FilePath: /flutterad/lib/view/ad_banner.dart
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import 'dart:io';

import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

/// Banner 广告组件
class AdBannerWidget extends StatefulWidget {
  const AdBannerWidget({
    Key? key,
    required this.posId,
    this.width = 300,
    this.height = 150,
    this.show = true,
  }) : super(key: key);
  // 广告 id
  final String posId;
  // 创建 Banner 广告位时选择的宽度，默认值是 300
  final int width;
  // 创建 Banner 广告位时选择的高度，默认值是 150
  final int height;
  // 是否显示广告
  final bool show;

  @override
  _AdBannerWidgetState createState() => _AdBannerWidgetState();
}

class _AdBannerWidgetState extends State<AdBannerWidget>
    with AutomaticKeepAliveClientMixin {
  // View 类型
  final String viewType = 'flutterad_banner';
  // 创建参数
  late Map<String, dynamic> creationParams;

  @override
  void initState() {
    creationParams = <String, dynamic>{
      "posId": widget.posId,
      "width": widget.width,
      "height": widget.height,
    };
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    super.build(context);
    if (!widget.show) {
      return const SizedBox.shrink();
    }
    if (Platform.isIOS) {
      return SizedBox.fromSize(
        size: Size(widget.width.toDouble(), widget.height.toDouble()),
        child: UiKitView(
          viewType: viewType,
          creationParams: creationParams,
          creationParamsCodec: const StandardMessageCodec(),
        ),
      );
    } else {
      return SizedBox.fromSize(
        size: Size(widget.width.toDouble(), widget.height.toDouble()),
        child: AndroidView(
          viewType: viewType,
          creationParams: creationParams,
          creationParamsCodec: const StandardMessageCodec(),
        ),
      );
    }
  }

  @override
  bool get wantKeepAlive => true;
}
