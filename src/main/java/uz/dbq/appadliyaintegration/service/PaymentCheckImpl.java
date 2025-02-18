package uz.dbq.appadliyaintegration.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentCheckImpl implements PaymentCheck {
    @PersistenceContext(unitName = "dataBaseThird")
    private final EntityManager entityManager;

    @PersistenceContext(unitName = "dataBaseFourth")
    private final EntityManager entityManagerFifth;

    @PersistenceContext(unitName = "dataBaseFirst")
    private final EntityManager entityManagerFourth;

    public PaymentCheckImpl(EntityManager entityManager,
                            EntityManager entityManagerFifth,
                            EntityManager entityManagerFourth) {
        this.entityManager = entityManager;
        this.entityManagerFifth = entityManagerFifth;
        this.entityManagerFourth = entityManagerFourth;
    }

    @Override
    public Double payment_v1(String g9inn) {
        String sql = "select value(sum(kre_r), 0) - value(sum(deb_r), 0) from tp.sal_cl where god=year(current_date) and sch='153' and inn=:g9inn ";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("g9inn", g9inn);

        Object result = query.getSingleResult();
        System.out.println("2");
        return result != null ? ((Number) result).doubleValue() : 0D;
    }

    @Override
    public Double payment_v3(String g9inn) {
        String sql = "select 'deb' as tip, value(sum(sum_r),0) as sum_r \n" +
                "from tp.tabunv \n" +
                "where (((D_VID1='ОП' or D_VID1='РП') and year(value(case\n" +
                "when d_vid1!='ОП' and d_vid1!='РП' then data\n" +
                "when data1 is null and (d_vid1='ОП' or d_vid1='РП') then data_ostr\n" +
                "when data1 is not null and date(updtime)<=data_ostr and data1<=data_ostr and (d_vid1='ОП' or d_vid1='РП') then data1\n" +
                "when data1 is not null and date(updtime)>data_ostr and data1>data_ostr and data_ostr>=data and (d_vid1='ОП' or d_vid1='РП') then data_ostr\n" +
                "when data1 is not null and date(updtime)>data_ostr and data1>data_ostr and data_ostr<data and (d_vid1='ОП' or d_vid1='РП') then data1\n" +
                "when data1 is not null and date(updtime)>data_ostr and data1<=data_ostr and data_ostr>=data and (d_vid1='ОП' or d_vid1='РП') then data_ostr\n" +
                "when data1 is not null and date(updtime)>data_ostr and data1<=data_ostr and data_ostr<data and (d_vid1='ОП' or d_vid1='РП') then data1 end,data))=year(current_date)) \n" +
                "or ((D_VID1!='ОП' and D_VID1!='РП') and year(data)=year(current_date)))\n" +
                " and deb_cl=:g9inn and sum_r<>0 and deb='153'\n" +
                "union\n" +
                "select 'kre' as tip, value(sum(sum_r),0) as sum_r \n" +
                "from tp.tabunv \n" +
                "where kre_cl=:g9inn and \n" +
                "year(data)=year(current_date) \n" +
                "and sum_r<>0\n" +
                "and kre='153'";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("g9inn", g9inn);

        List<Object[]> results = query.getResultList();

        Double deb = 0D;
        Double kre = 0D;

        for (Object[] result : results) {
            String tip = (String) result[0];
            Double sum_r = ((Number) result[1]).doubleValue();

            if ("deb".equals(tip)) {
                deb = sum_r;
            } else if ("kre".equals(tip)) {
                kre = sum_r;
            }
        }

        return kre - deb;
    }

    @Override
    public Double payment_v5(String g9inn) {
        String sql = "select value(sum(p.g47sum),0) sum3 from ASOD.DECLARATION d \n" +
                "join ASOD.COMMODITY c on c.DECL_ID=d.id \n" +
                "join ASOD.PAYMENT p on p.CMDT_ID=c.id and p.G47SP!='ОО' and p.G47SP!='УН' and p.G47SP not in ('ОП','РП') \n" +
                "where d.G28_1C=:g9inn and d.STATUS='1' and d.FLAG_R<>'3' and d.FLAG_R<>'2' and d.GDVIPDATE is null";

        Query query = entityManagerFifth.createNativeQuery(sql);
        query.setParameter("g9inn", g9inn);

        Object result = query.getSingleResult();


        return result != null ? ((Number) result).doubleValue() : 0D;
    }

    @Override
    public Double payment_v6(String g9inn) {
        Object result = null;

        if (big_organization(g9inn) || big_organization_mat(g9inn)) {
            String sql = "select d.ID from ASOD.DECLARATION d where  d.STATUS='1' and d.GDVIPDATE is null and d.G28_1C=:g9inn";

            StringBuilder declStr = new StringBuilder();
            Query query = entityManagerFifth.createNativeQuery(sql);
            query.setParameter("g9inn".toString(), g9inn);
            List resultList = query.getResultList();
            for (Object sItem : resultList) {
                declStr.append("'").append(sItem).append("',");
            }

            if (!(declStr == null || declStr.length() == 0)) {
                declStr = new StringBuilder(declStr.substring(0, declStr.length() - 1));
                sql = "select value(sum(m.PEREPLATA),sum(m1.PEREPLATA),0) \n" +
                        "from asod.declaration d \n" +
                        "left join gnk_money.METOD_1 m on m.DECL_ID=d.id and m.SUCCESS=5\n" +
                        "left join gnk_money.METOD279_1 m1 on (m1.DECL_ID=d.id or (m1.G7_A=d.G7A and m1.G7_B=d.G7B and m1.G7_C=d.G7C)) and m1.SUCCESS=5 and m1.PAY_SUM_RECEIVE=1\n" +
                        "where d.id in (" + declStr + ")";

                Query query2 = entityManagerFifth.createNativeQuery(sql);
                result = query2.getSingleResult();
            }

        }
        return result != null ? ((Number) result).doubleValue() : 0D;
    }

    @Override
    public boolean big_organization(String g9inn) {
        String sql = "select count(*) cnt from tp.KNP k where k.STATUS_GTK='0' and k.inn=:g9inn";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("g9inn".toString(), g9inn);

        Object result = query.getSingleResult();


        return result != null && ((Number) result).intValue() > 0;
    }

    @Override
    public boolean big_organization_mat(String g9inn) {
        String sql = " select count(*) tek_inn from GNK_NEW.TINGROUP_A t where date(t.DATE)=current_date - 1 day and t.TIN=:g9inn";

        Query query = entityManagerFourth.createNativeQuery(sql);
        query.setParameter("g9inn", g9inn);

        Object result = query.getSingleResult();


        return result != null && ((Number) result).intValue() > 0;
    }

    public Double result(String g9inn) {
        Double aDouble_v1 = payment_v1(g9inn); // k-d ;
        Double aDouble_v3 = payment_v3(g9inn); // -d ;
        Double aDouble_v5 = payment_v5(g9inn); // -d ;
        Double aDouble_v6 = payment_v6(g9inn); // +k ;

        double v = aDouble_v1 + aDouble_v3 + aDouble_v6 - aDouble_v5;
        return v;
    }
}
