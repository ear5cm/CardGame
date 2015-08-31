package com.baili_feng.cardgame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.util.Log;

/**
 * Created by baili_feng on 2015/8/31.
 */
public class Rule {
    private static final int BAIDA = -1;
    private static final String TAG = "Rule";

    private static Rule mInstance = null;

    public CardGroup mTiao = null;
    public CardGroup mBing = null;
    public CardGroup mWan = null;

    public class CardGroup
    {
        public boolean jiang;
        public int baida;
        public List<Integer> cards = new ArrayList<>();
    }

    public static Rule getInstance() {
        if(mInstance == null) {
            mInstance = new Rule();
        }
        return mInstance;
    }

    public Rule() {
    }

    public List<Integer> checkHu(List<List<Integer>> list, int numBaida) {
        List<Integer> result = new ArrayList<>();
        if(list.size() != 3) {
            return null;
        }

        List<CardGroup> vcg = null;
        CardGroup cg = new CardGroup();
        cg.jiang = false;
        cg.baida = numBaida;
        for(int i = 0; i < 3; i++) {
            if(list.get(i).size() > 0)
            {
                cg.cards.clear();
                cg.cards.addAll(list.get(i));
                vcg = CheckGroup(cg);
                if(vcg.size() <= 0)
                {
                    Log.e(TAG, "[" + i + "]not hu. " + " baida: " + cg.baida + ". list: " + cg.cards);
                    return null;
                }
                cg.jiang |= vcg.get(0).jiang;
                cg.baida -= vcg.get(0).baida;
                result.addAll(vcg.get(0).cards);
            }
        }
        Log.i(TAG, "hu result: " + result);
        return result;
    }

    CardGroup createAX(int v1, boolean jiang)
    {
        CardGroup cg = new CardGroup();
        cg.cards.add(v1);
        cg.cards.add(BAIDA);
        cg.jiang = true;
        cg.baida = 1;
        return cg;
    }
    CardGroup createAXX(int v1, boolean jiang)
    {
        CardGroup cg = new CardGroup();
        cg.cards.add(v1);
        cg.cards.add(BAIDA);
        cg.cards.add(BAIDA);
        cg.jiang = false || jiang;
        cg.baida = 2;
        return cg;
    }
    CardGroup createAA(int v1, int v2, boolean jiang)
    {
        CardGroup cg = new CardGroup();
        cg.cards.add(v1);
        cg.cards.add(v2);
        cg.jiang = true;
        cg.baida = 0;
        return cg;
    }
    CardGroup createAAX(int v1, int v2, boolean jiang)
    {
        CardGroup cg = new CardGroup();
        cg.cards.add(v1);
        cg.cards.add(v2);
        cg.cards.add(BAIDA);
        cg.jiang = false || jiang;
        cg.baida = 1;
        return cg;
    }

    CardGroup createABX(int v1, int v2, boolean jiang)
    {
        return createAAX(v1, v2, jiang);
    }

    CardGroup createAXDXX(int v1, int v2, boolean jiang)
    {
        CardGroup cg = new CardGroup();
        cg.cards.add(v1);
        cg.cards.add(BAIDA);
        cg.cards.add(v2);
        cg.cards.add(BAIDA);
        cg.cards.add(BAIDA);
        cg.jiang = true;
        cg.baida = 3;
        return cg;
    }

    CardGroup createAXXDXX(int v1, int v2, boolean jiang)
    {
        CardGroup cg = new CardGroup();
        cg.cards.add(v1);
        cg.cards.add(BAIDA);
        cg.cards.add(BAIDA);
        cg.cards.add(v2);
        cg.cards.add(BAIDA);
        cg.cards.add(BAIDA);
        cg.jiang = false || jiang;
        cg.baida = 4;
        return cg;
    }

    CardGroup createAAA(int v1, int v2, int v3, boolean jiang)
    {
        CardGroup cg = new CardGroup();
        cg.cards.add(v1);
        cg.cards.add(v2);
        cg.cards.add(v3);
        cg.jiang = false || jiang;
        cg.baida = 0;
        return cg;
    }

    CardGroup createABC(int v1, int v2, int v3, boolean jiang)
    {
        return createAAA(v1, v2, v3, jiang);
    }

    CardGroup createABBCX(int v1, int v2, int v3, int v4, boolean jiang)
    {
        CardGroup cg = new CardGroup();
        cg.cards.add(v1);
        cg.cards.add(v2);
        cg.cards.add(v4);
        cg.cards.add(v3);
        cg.cards.add(BAIDA);
        cg.jiang = true;
        cg.baida = 1;
        return cg;
    }

    CardGroup createABBBC(int v1, int v2, int v3, int v4, int v5, boolean jiang)
    {
        CardGroup cg = new CardGroup();
        cg.cards.add(v1);
        cg.cards.add(v2);
        cg.cards.add(v5);
        cg.cards.add(v3);
        cg.cards.add(v4);
        cg.jiang = true;
        cg.baida = 0;
        return cg;
    }

    CardGroup createABBBCX(int v1, int v2, int v3, int v4, int v5, boolean jiang)
    {
        CardGroup cg = new CardGroup();
        cg.cards.add(v1);
        cg.cards.add(v2);
        cg.cards.add(v5);
        cg.cards.add(v3);
        cg.cards.add(v4);
        cg.cards.add(BAIDA);
        cg.jiang = false || jiang;
        cg.baida = 1;
        return cg;
    }

    CardGroup createAABXCC(int v1, int v2, int v3, int v4, int v5, boolean jiang)
    {
        CardGroup cg = new CardGroup();
        cg.cards.add(v1);
        cg.cards.add(v3);
        cg.cards.add(v5);
        cg.cards.add(v2);
        cg.cards.add(v4);
        cg.cards.add(BAIDA);
        cg.jiang = false || jiang;
        cg.baida = 1;
        return cg;
    }

    CardGroup createAABBCX(int v1, int v2, int v3, int v4, int v5, boolean jiang)
    {
        CardGroup cg = new CardGroup();
        cg.cards.add(v1);
        cg.cards.add(v2);
        cg.cards.add(v3);
        cg.cards.add(v4);
        cg.cards.add(v5);
        cg.cards.add(BAIDA);
        cg.jiang = false || jiang;
        cg.baida = 1;
        return cg;
    }

    CardGroup createAXBBCC(int v1, int v2, int v3, int v4, int v5, boolean jiang)
    {
        CardGroup cg = new CardGroup();
        cg.cards.add(v1);
        cg.cards.add(BAIDA);
        cg.cards.add(v2);
        cg.cards.add(v3);
        cg.cards.add(v4);
        cg.cards.add(v5);
        cg.jiang = false || jiang;
        cg.baida = 1;
        return cg;
    }

    CardGroup createAABBCC(int v1, int v2, int v3, int v4, int v5, int v6, boolean jiang)
    {
        CardGroup cg = new CardGroup();
        cg.cards.add(v1);
        cg.cards.add(v3);
        cg.cards.add(v5);
        cg.cards.add(v2);
        cg.cards.add(v4);
        cg.cards.add(v6);
        cg.jiang = false || jiang;
        cg.baida = 0;
        return cg;
    }

    CardGroup createABBBBC(int v1, int v2, int v3, int v4, int v5, int v6, boolean jiang)
    {
        CardGroup cg = new CardGroup();
        cg.cards.add(v1);
        cg.cards.add(v2);
        cg.cards.add(v6);
        cg.cards.add(v3);
        cg.cards.add(v4);
        cg.cards.add(v5);
        cg.jiang = false || jiang;
        cg.baida = 0;
        return cg;
    }

    CardGroup createABBCCD(int v1, int v2, int v3, int v4, int v5, int v6, boolean jiang)
    {
        CardGroup cg = new CardGroup();
        cg.cards.add(v1);
        cg.cards.add(v2);
        cg.cards.add(v4);
        cg.cards.add(v3);
        cg.cards.add(v5);
        cg.cards.add(v6);
        cg.jiang = false || jiang;
        cg.baida = 0;
        return cg;
    }

    CardGroup createAAAABBBBCCCC(int v1, int v2, int v3, boolean jiang)
    {
        CardGroup cg = new CardGroup();
        cg.cards.add(v1);
        cg.cards.add(v1);
        cg.cards.add(v1);
        cg.cards.add(v1);
        cg.cards.add(v2);
        cg.cards.add(v2);
        cg.cards.add(v2);
        cg.cards.add(v2);
        cg.cards.add(v3);
        cg.cards.add(v3);
        cg.cards.add(v3);
        cg.cards.add(v3);
        cg.jiang = false || jiang;
        cg.baida = 0;
        return cg;
    }

    List< CardGroup >	Check3Cards(CardGroup group)
    {
        List< CardGroup > vgroup = new ArrayList<>();
        int v1 = group.cards.get(0);
        int v2 = group.cards.get(1);
        int v3 = group.cards.get(2);
        //AAA
        if(v1 == v2 && v2 == v3)
        {
            vgroup.add(createAAA(v1, v2, v3, group.jiang));
        }
        else if(v1 == v2-1 && v2 == v3-1)
        {
            vgroup.add(createABC(v1, v2, v3, group.jiang));
        }
        return vgroup;
    }

    List< CardGroup >	Check4Cards(CardGroup group)
    {
        List< CardGroup > vgroup = new ArrayList<>();
        int v1 = group.cards.get(0);
        int v2 = group.cards.get(1);
        int v3 = group.cards.get(2);
        int v4 = group.cards.get(3);
        if(v1 == v2-1 && v2 == v3 && v3 == v4-1)
        {
            if(group.jiang == false && group.baida > 0)
            {
                vgroup.add(createABBCX(v1, v2, v3, v4, group.jiang));
            }
        }
        return vgroup;
    }

    List< CardGroup >	Check5Cards(CardGroup group)
    {
        List< CardGroup > vgroup = new ArrayList<>();
        int v1 = group.cards.get(0);
        int v2 = group.cards.get(1);
        int v3 = group.cards.get(2);
        int v4 = group.cards.get(3);
        int v5 = group.cards.get(4);
        if(v1 == v2-1 && v2 == v3 && v3 == v4 && v4 == v5-1)
        {
            if(group.jiang == false)
            {
                vgroup.add(createABBBC(v1, v2, v3, v4, v5, group.jiang));
            }
            if(group.baida > 0)
            {
                vgroup.add(createABBBCX(v1, v2, v3, v4, v5, group.jiang));
            }
        }
        else if(v1 == v2 && v2 == v3-1 && v3 == v4-1 && v4 == v5)
        {
            if(group.baida > 0)
            {
                vgroup.add(createAABXCC(v1, v2, v3, v4, v5, group.jiang));
            }
        }
        else if(v1 == v2 && v2 == v3-1 && v3 == v4 && v4 == v5-1)
        {
            if(group.baida > 0)
            {
                vgroup.add(createAABBCX(v1, v2, v3, v4, v5, group.jiang));
            }
        }
        else if(v1 == v2-1 && v2 == v3 && v3 == v4-1 && v4 == v5)
        {
            if(group.baida > 0)
            {
                vgroup.add(createAXBBCC(v1, v2, v3, v4, v5, group.jiang));
            }
        }
        return vgroup;
    }

    List< CardGroup >	Check6Cards(CardGroup group)
    {
        List< CardGroup > vgroup = new ArrayList<>();
        int v1 = group.cards.get(0);
        int v2 = group.cards.get(1);
        int v3 = group.cards.get(2);
        int v4 = group.cards.get(3);
        int v5 = group.cards.get(4);
        int v6 = group.cards.get(5);
        if(v1 == v2 && v3 == v4 && v5 == v6 &&
                v1 == v3-1 && v3 == v5-1)
        {
            vgroup.add(createAABBCC(v1, v2, v3, v4, v5, v6, group.jiang));
        }
        else if(v1 == v2-1 && v2 == v3 && v3 == v4 && v4 == v5 && v5 == v6-1)
        {
            vgroup.add(createABBBBC(v1, v2, v3, v4, v5, v6, group.jiang));
        }
        else if(v1 == v2-1 && v2 == v3 && v3 == v4-1 && v4 == v5 && v5 == v6-1)
        {
            vgroup.add(createABBCCD(v1, v2, v3, v4, v5, v6, group.jiang));
        }
        return vgroup;
    }

    List< CardGroup >	Check12Cards(CardGroup group)
    {
        List< CardGroup > vgroup = new ArrayList<>();
        int v1 = group.cards.get(0);
        int v2 = group.cards.get(1);
        int v3 = group.cards.get(2);
        int v4 = group.cards.get(3);
        int v5 = group.cards.get(4);
        int v6 = group.cards.get(5);
        int v7 = group.cards.get(6);
        int v8 = group.cards.get(7);
        int v9 = group.cards.get(8);
        int v10 = group.cards.get(9);
        int v11 = group.cards.get(10);
        int v12 = group.cards.get(11);
        if(v1 == v2 && v2 == v3 && v3 == v4 && v4 == v5-1 &&
                v5 == v6 && v6 == v7 && v7 == v8 && v8 == v9-1 &&
                v9 == v10 && v10 == v11 && v11 == v12)
        {
            vgroup.add(createAAAABBBBCCCC(v1, v5, v9, group.jiang));
        }
        return vgroup;
    }

    List< CardGroup > MergeGroup(List< CardGroup > group1, List< CardGroup > group2)
    {
        List< CardGroup > vgroup = new ArrayList<>();
        CardGroup cg = null;
        CardGroup tmp = null;
        int minBaida = 100;
        for (Iterator<CardGroup> it = group1.iterator(); it.hasNext();) {
            tmp = it.next();
            if(tmp.baida < minBaida)
            {
                minBaida = tmp.baida;
                cg = tmp;
            }
        }
        for (Iterator<CardGroup> it = group2.iterator(); it.hasNext();) {
            tmp = it.next();
            if(tmp.baida < minBaida)
            {
                minBaida = tmp.baida;
                cg = tmp;
            }
        }
        if(cg != null)
        {
            vgroup.add(cg);
        }
        return vgroup;
    }

    List< CardGroup > CheckMinusNGroup(CardGroup group, int n)
    {
        List< CardGroup > vgroup = new ArrayList<>();

        CardGroup cg = new CardGroup();
        cg.baida = group.baida;
        cg.jiang = group.jiang;

        CardGroup cg2 = new CardGroup();
        cg2.baida = group.baida;
        cg2.jiang = group.jiang;
        int i = 0;
        for (Iterator<Integer> it = group.cards.iterator(); it.hasNext();) {
            // first half and second half
            int tmp = it.next();
            if(i < n) cg.cards.add(tmp);
            else cg2.cards.add(tmp);
            i++;
        }

        List< CardGroup > vcg =  CheckGroup(cg);
        for (Iterator<CardGroup> it = vcg.iterator(); it.hasNext();) {
            CardGroup tmp = it.next();

            cg2.jiang = tmp.jiang;
            cg2.baida = group.baida - tmp.baida;
            List< CardGroup > vcg2 =  CheckGroup(cg2);
            for (Iterator<CardGroup> it2 = vcg2.iterator(); it2.hasNext();) {
                CardGroup tmp2 = it2.next();

                CardGroup cg3 = new CardGroup();
                cg3.cards.addAll(tmp.cards);
                cg3.cards.addAll(tmp2.cards);
                cg3.jiang = tmp.jiang || tmp2.jiang;
                cg3.baida = tmp.baida + tmp2.baida;
                boolean found = false;
                for (Iterator<CardGroup> it3 = vgroup.iterator(); it3.hasNext();) {
                    CardGroup tmp3 = it3.next();
                    if(tmp3.jiang == cg3.jiang && tmp3.baida == cg3.baida)
                    {
                        found = true;
                    }
                }
                if(found == false)
                {
                    vgroup.add(cg3);
                }
            }
        }
        return vgroup;
    }

    List< CardGroup > CheckGroup(CardGroup group)
    {
        // X is baida
        List< CardGroup > vgroup = new ArrayList<>();
        int size = group.cards.size();
        if(size == 1)
        {
            int v1 = group.cards.get(0);
            // AX
            if(group.baida > 0 && group.jiang == false)
            {
                vgroup.add(createAX(v1, group.jiang));
            }
            //AXX
            if(group.baida > 1)
            {
                vgroup.add(createAXX(v1, group.jiang));
            }
            return vgroup;
        }
        else if(size == 2)
        {
            int v1 = group.cards.get(0);
            int v2 = group.cards.get(1);
            if(v1 == v2)
            {
                if(group.jiang == false)
                {
                    //AA
                    {
                        vgroup.add(createAA(v1, v2, group.jiang));
                    }
                    //AAX
                    if(group.baida > 0)
                    {
                        vgroup.add(createAAX(v1, v2, group.jiang));
                    }
                }
                else if(group.baida > 0)
                {
                    //AAX
                    vgroup.add(createAAX(v1, v2, group.jiang));
                }
            }
            else if(v2 - v1 <=2)
            {
                //ABX AXB etc
                if(group.baida > 0)
                {
                    vgroup.add(createABX(v1, v2, group.jiang));
                }
            }
            else
            {
                if(group.jiang == false)
                {
                    //AXDXX AXXDX etc
                    if(group.baida >= 3)
                    {
                        vgroup.add(createAXDXX(v1, v2, group.jiang));
                    }
                    //AXXDXX
                    if(group.baida >= 4)
                    {
                        vgroup.add(createAXXDXX(v1, v2, group.jiang));
                    }
                }
                else
                {
                    //AXXDXX
                    if(group.baida >= 4)
                    {
                        vgroup.add(createAXXDXX(v1, v2, group.jiang));
                    }
                }
            }
            return vgroup;
        }
        List< CardGroup > vcg;
        if(size >= 12)
        {
            if(size == 12)
            {
                vcg = Check12Cards(group);
                vgroup = MergeGroup(vgroup, vcg);
            }
            else
            {
                vcg = CheckMinusNGroup(group, 12);
                vgroup = MergeGroup(vgroup, vcg);
            }
        }
        if(size >= 6)
        {
            if(size == 6)
            {
                vcg = Check6Cards(group);
                vgroup = MergeGroup(vgroup, vcg);
            }
            else
            {
                vcg = CheckMinusNGroup(group, 6);
                vgroup = MergeGroup(vgroup, vcg);
            }
        }
        if(size >= 5)
        {
            if(size == 5)
            {
                vcg = Check5Cards(group);
                vgroup = MergeGroup(vgroup, vcg);
            }
            else
            {
                vcg = CheckMinusNGroup(group, 5);
                vgroup = MergeGroup(vgroup, vcg);
            }
        }
        if(size >= 4)
        {
            if(size == 4)
            {
                vcg = Check4Cards(group);
                vgroup = MergeGroup(vgroup, vcg);
            }
            else
            {
                vcg = CheckMinusNGroup(group, 4);
                vgroup = MergeGroup(vgroup, vcg);
            }
        }
        if(size >= 3)
        {
            if(size == 3)
            {
                vcg = Check3Cards(group);
                vgroup = MergeGroup(vgroup, vcg);
            }
            else
            {
                vcg = CheckMinusNGroup(group, 3);
                vgroup = MergeGroup(vgroup, vcg);
            }
        }

        vcg = CheckMinusNGroup(group, 2);
        vgroup = MergeGroup(vgroup, vcg);

        vcg = CheckMinusNGroup(group, 1);
        vgroup = MergeGroup(vgroup, vcg);

        return vgroup;
    }

}
