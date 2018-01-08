# JDAddressSelector
### app ---> dbflow           appforsql ---> native code
级联地址选择器。
![image](https://raw.githubusercontent.com/eric-zhang-dev/AddressSelector/master/screenshots/aa4.png)

## 添加依赖

项目的 `build.gradle` 中：

    allprojects {
        repositories {
            ...
            maven { url "https://jitpack.io"}
        }
    }
    
## 使用方法
        1
        AddressSelector selector = new AddressSelector(this, null, null, null, null, null, null);
        selector.setOnAddressSelectedListener(this);


        2
        buttonBottomDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ~~BottomDialog.show(MainActivity.this, MainActivity.this);~~
                BottomDialog dialog = new BottomDialog(MainActivity.this, "江苏省", "苏州市", "吴中区", "越溪街道", "越溪管理区", "木林社区");
                dialog.setOnAddressSelectedListener(MainActivity.this);
                dialog.show();
            }
        });
        3
            @Override
            public void onAddressSelected(String address, String id) {
                T.showShort(this, address + "---->" + id);
            }
    
#### 相关依赖

- **com.android.support**：Google官方适配包，用于提供卡片、列表、主题等基础模块
- **com.google.code.gson**：Json-Model解析库
- **com.github.Raizlabs.DBFlow**:数据库  [dbflow教程](https://yumenokanata.gitbooks.io/dbflow-tutorials/content/tables_and_database_properties.html)

## 许可证
--