package com.jojo.svm;
public class SVMClassifier{
    public static void main(String[] args) throws Exception {
        /*
        train1.txt 存放SVM训练模型用的数据的路径
        model_r.txt存放SVM通过训练数据训练出来的模型的路径
         */
        String[] arg = {"trainfile\\train1.txt","trainfile\\model_r.txt"};

        /*
        train2.txt这个是存放测试数据
        model_r.txt调用的是训练以后的模型
        out_r.txt生成结果的文件的路径
         */
        String[] parg = {"trainfile\\train2.txt","trainfile\\model_r.txt","trainfile\\out_r.txt"};

        System.out.println("-------SVM Start Running--------");

        svm_train t = new svm_train();//创建一个训练对象

        svm_predict p = new svm_predict();//创建一个预测或者分类的对象
        t.main(arg);//执行训练
        p.main(parg);//
    }
}