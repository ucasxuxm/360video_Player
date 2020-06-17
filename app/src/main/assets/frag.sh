#extension GL_OES_EGL_image_external : require  //声明GLES11Ext.GL_TEXTURE_EXTERNAL_OES扩展可用
precision mediump float;
varying vec2 vTextureCoord; //接收从顶点着色器过来的参数
uniform samplerExternalOES sTexture;//纹理内容数据
void main()
{
   //给此片元从纹理中采样出颜色值
   gl_FragColor = texture2D(sTexture, vTextureCoord);
}