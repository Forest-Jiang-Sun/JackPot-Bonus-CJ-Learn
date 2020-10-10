package com.aspectgaming.gdx.component.drawable.reel;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Blur filter Effect.
 * 
 * @author ligang.yao
 *
 */
public interface MotionBlurShaderSpec {

    String VERTEX = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
                  "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" +
                  "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" +

                  "uniform mat4 u_projTrans;\n" +
                  " \n" +
                  "varying vec4 vColor;\n" +
                  "varying vec2 vTexCoord;\n" +

                  "void main() {\n" +
                  "   vColor = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" +
                  "   vTexCoord = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" +
                  "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
                  "}";

    String FRAGMENT_8 = "varying vec4 vColor;\n" +
                       "varying vec2 vTexCoord;\n" +
                       "\n" +
                       "uniform sampler2D u_texture;\n" +
                       "uniform float resolution;\n" +
                       "uniform float radius;\n" +
                       "\n" +
                       "void main() {\n" +
                       "   vec4 sum = vec4(0.0);\n" +
                       "   vec2 tc = vTexCoord;\n" +
                       "   float blur = radius/resolution; \n" +
                       "    \n" +
                       "   sum += texture2D(u_texture, vec2(tc.x, tc.y - 8.0*blur)) * 0.01;\n" +
                       "   sum += texture2D(u_texture, vec2(tc.x, tc.y - 7.0*blur)) * 0.02;\n" +
                       "   sum += texture2D(u_texture, vec2(tc.x, tc.y - 6.0*blur)) * 0.03;\n" +
                       "   sum += texture2D(u_texture, vec2(tc.x, tc.y - 5.0*blur)) * 0.04;\n" +
                       "   sum += texture2D(u_texture, vec2(tc.x, tc.y - 4.0*blur)) * 0.05;\n" +
                       "   sum += texture2D(u_texture, vec2(tc.x, tc.y - 3.0*blur)) * 0.06;\n" +
                       "   sum += texture2D(u_texture, vec2(tc.x, tc.y - 2.0*blur)) * 0.07;\n" +
                       "   sum += texture2D(u_texture, vec2(tc.x, tc.y - 1.0*blur)) * 0.08;\n" +
                       "   sum += texture2D(u_texture, vec2(tc.x, tc.y)) * 0.64;\n" +
                       "\n" +
                       "   gl_FragColor = vColor * vec4(sum.rgb, sum.a);\n" +
                       "}";

    String FRAGMENT_16 = "varying vec4 vColor;\n" +
                       "varying vec2 vTexCoord;\n" +
                       "\n" +
                       "uniform sampler2D u_texture;\n" +
                       "uniform float resolution;\n" +
                       "uniform float radius;\n" +
                       "\n" +
                       "void main() {\n" +
                       "   vec4 sum = vec4(0.0);\n" +
                       "   vec2 tc = vTexCoord;\n" +
                       "   float blur = radius/resolution; \n" +
                       "    \n" +
                       "   sum += texture2D(u_texture, vec2(tc.x, tc.y - 16.0*blur)) * 0.01;\n" +
                       "   sum += texture2D(u_texture, vec2(tc.x, tc.y - 15.0*blur)) * 0.015;\n" +
                       "   sum += texture2D(u_texture, vec2(tc.x, tc.y - 14.0*blur)) * 0.02;\n" +
                       "   sum += texture2D(u_texture, vec2(tc.x, tc.y - 13.0*blur)) * 0.025;\n" +
                       "   sum += texture2D(u_texture, vec2(tc.x, tc.y - 12.0*blur)) * 0.03;\n" +
                       "   sum += texture2D(u_texture, vec2(tc.x, tc.y - 11.0*blur)) * 0.035;\n" +
                       "   sum += texture2D(u_texture, vec2(tc.x, tc.y - 10.0*blur)) * 0.04;\n" +
                       "   sum += texture2D(u_texture, vec2(tc.x, tc.y - 9.0*blur)) * 0.045;\n" +
                       "   sum += texture2D(u_texture, vec2(tc.x, tc.y - 8.0*blur)) * 0.05;\n" +
                       "   sum += texture2D(u_texture, vec2(tc.x, tc.y - 7.0*blur)) * 0.055;\n" +
                       "   sum += texture2D(u_texture, vec2(tc.x, tc.y - 6.0*blur)) * 0.06;\n" +
                       "   sum += texture2D(u_texture, vec2(tc.x, tc.y - 5.0*blur)) * 0.065;\n" +
                       "   sum += texture2D(u_texture, vec2(tc.x, tc.y - 4.0*blur)) * 0.07;\n" +
                       "   sum += texture2D(u_texture, vec2(tc.x, tc.y - 3.0*blur)) * 0.075;\n" +
                       "   sum += texture2D(u_texture, vec2(tc.x, tc.y - 2.0*blur)) * 0.08;\n" +
                       "   sum += texture2D(u_texture, vec2(tc.x, tc.y - 1.0*blur)) * 0.085;\n" +
                       "   sum += texture2D(u_texture, vec2(tc.x, tc.y)) * 0.24;\n" +
                       "\n" +
                       "   gl_FragColor = vColor * vec4(sum.rgb, sum.a);\n" +
                       "}";
}
