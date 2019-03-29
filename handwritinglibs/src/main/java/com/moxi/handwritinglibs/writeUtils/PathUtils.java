package com.moxi.handwritinglibs.writeUtils;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import com.moxi.handwritinglibs.model.WriteModel.WLine;
import com.moxi.handwritinglibs.model.WriteModel.WPoint;

import java.util.List;

/**
 * path类处理
 * Created by xj on 2017/12/5.
 */

public class PathUtils {
    /**
     * 判断path与rectF是否相交，删除函数
     *
     * @param rectF  矩形区域
     * @param pModel
     * @return
     */
    public static boolean getPathIntersect(RectF rectF, WLine pModel) {
        //第一步判断包含关系，如果
        List<WPoint> points=pModel.getPoints();
        if (points.size()==0)return true;
        if (points.size()==1){
            return rectF.contains(points.get(0).x,points.get(0).y);
        }
        RectF pRect = pModel.getRectF();
        //判断矩形区域是否相包含 不包含排除计算
        boolean contain= rectF.intersect(pRect);
        Path path=new Path();
        if (contain) {
//            APPLog.e("getPathIntersect","包含在里面");
            //当点多于1的时候对比计算点与线段信息
            for (int i = 1; i < points.size(); i++) {
                if (getRectUnLine(rectF,points.get(i-1),points.get(i))){
                    return true;
                }
            }

        }else {
//            APPLog.e("getPathIntersect","不包含在里面");
        }
        return false;
    }

    /**
     * 判断矩形框是否与线段相交
     *
     * @param rectF
     * @param p1
     * @param p2
     * @return
     */
    private static boolean getRectUnLine(RectF rectF, WPoint p1, WPoint p2) {
        //矩形框的四个顶点
        float rtop = rectF.top;
        float rleft = rectF.left;
        float rringht = rectF.right;
        float rbottom = rectF.bottom;

        //判断域外线段信息直接返回
        if ((p1.x<rleft&&p2.x<rleft)
                ||(p1.x>rringht&&p2.x>rringht)
                ||(p1.y>rbottom&&p2.y>rbottom)
                ||(p1.y<rtop&&p2.y<rtop)){
            return false;
        }else if (rectF.contains(p1.x,p1.y)&&rectF.contains(p2.x,p2.y)){
            //包含线段返回
            return true;
        }
        /**
         *
         * 判断线与线相交
         */
        if (getLineUnLine(p1, p2, rleft, rtop, rringht, rtop)
                || getLineUnLine(p1, p2, rringht, rtop, rringht, rbottom)
                || getLineUnLine(p1, p2, rringht, rbottom, rleft, rbottom)
                || getLineUnLine(p1, p2, rleft, rbottom, rleft, rtop)) {
            return true;
        }
        return false;
    }

    /**
     * 计算线段与线段是否相交,两组线段坐标--------------开始
     *
     * @return
     */
    private static boolean getLineUnLine(float line1x, float line1y, float line12x, float line12y,
                                        float line21x, float line21y, float line22x, float line22y) {
        return doIntersect(new PointF(line1x, line1y),
                new PointF(line12x, line12y),
                new PointF(line21x, line21y),
                new PointF(line22x, line22y));
    }

    private static boolean getLineUnLine(WPoint p1, WPoint p2,
                                        float line21x, float line21y, float line22x, float line22y) {
        return getLineUnLine(p1.x, p1.y, p2.x, p2.y, line21x, line21y, line22x, line22y);
    }


    // Given three colinear points p, q, r, the function checks if point q lies on line segment 'pr'
    private static boolean onSegment(PointF p, PointF q, PointF r) {
        if (q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) &&
                q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y))
            return true;
        return false;
    }


    /**
     * To find orientation of ordered triplet (p, q, r).
     * The function returns following values
     *
     * @param p
     * @param q
     * @param r
     * @return 0 --> p, q and r are colinear, 1 --> 顺时针方向, 2 --> 逆时钟方向
     */
    private static int orientation(PointF p, PointF q, PointF r) {
        // See http://www.geeksforgeeks.org/orientation-3-ordered-points/  for details of below formula.
        float val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
        if (val == 0) return 0;  // colinear
        return (val > 0) ? 1 : 2; // clock or counterclock wise
    }

    /**
     * 判断两条线段是否相交
     *
     * @param p1
     * @param q1
     * @param p2
     * @param q2
     * @return
     */
    private static boolean doIntersect(PointF p1, PointF q1, PointF p2, PointF q2) {

        // Find the four orientations needed for general and special cases
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);
        // General case
        if (o1 != o2 && o3 != o4) {
            return true;
        }
        // Special Cases
        // p1, q1 and p2 are colinear and p2 lies on segment p1q1
        if (o1 == 0 && onSegment(p1, p2, q1)) return true;
        // p1, q1 and p2 are colinear and q2 lies on segment p1q1
        if (o2 == 0 && onSegment(p1, q2, q1)) return true;
        // p2, q2 and p1 are colinear and p1 lies on segment p2q2
        if (o3 == 0 && onSegment(p2, p1, q2)) return true;
        // p2, q2 and q1 are colinear and q1 lies on segment p2q2
        if (o4 == 0 && onSegment(p2, q1, q2)) return true;

        return false; // Doesn't fall in any of the above cases
    }
    /**
     * 计算线段是否相交结束-----------------------------------------
     */

    /**
     * 获得以point为中心的举行框
     *
     * @param point 点击点
     * @param width
     * @return
     */
    private static RectF getRectF(PointF point, int width) {
        int ba = width / 2;
        return new RectF(point.x - ba, point.y - ba, point.x + ba, point.y + ba );
    }

    /**
     * 删除点框
     * @param point 点击点
     * @param width 删除框宽度
     * @return 返回删除路径
     */
    private static Path getRectPath(PointF point,int width){
        Path path=new Path();
        path.addRect(getRectF(point,width),Path.Direction.CW);
        return path;
    }
    /**
     * 框
     * @return 返回删除路径
     */
    private static Path getRectPath(RectF rectF){
        Path path=new Path();
        path.addRect(rectF,Path.Direction.CW);
        return path;
    }

    /**
     * 通过两点获得矩形框
     * @return
     */
    private static RectF getRectF(PointF start , PointF end) {
        return new RectF(start.x, start.y , end.x , end.y );
    }

    /**
     * 两点获得矩形框路径
     * @param start 开始点
     * @param end 结束点
     * @return 返回删除路径
     */
    private static Path getRectPath(PointF start , PointF end){
        Path path=new Path();
        path.addRect(getRectF(start,end),Path.Direction.CW);
        return path;
    }
}
