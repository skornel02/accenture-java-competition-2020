package org.ajc2020.backend.service;

import org.ajc2020.backend.model.Workstation;
import org.ajc2020.utility.resource.WorkerStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public class PlanRendererServiceImpl implements PlanRendererService {

    private static final String TRANSFORMATION_FORMULA = "matrix(%d, %d, %d, %d, %.7f, %.7f)";

    private static final String WORKSTATIONS_PLACEHOLDER = "<!--<workstations/>-->";
    private static final String WORKSTATION_TRANSFORMATION_PLACEHOLDER = "$workstation-transformation";
    private static final String WORKSTATION_ID_PLACEHOLDER = "$workstation-id";
    private static final String WORKSTATION_CLASS_PLACEHOLDER = "$workstation-class";
    private static final String WORKSTATION_FILL_PLACEHOLDER = "$workstation-fill";

    //language=SVG
    private static final String TEMPLATE_2D_PLAN =
            "<svg " +
            "xmlns=\"http://www.w3.org/2000/svg\"\n" +
            "     id=\"svg8\"\n" +
            "     viewBox=\"0 0 210 297\"\n" +
//            "     height=\"297mm\"\n" +
//                    "     width=\"210mm\"\n" +
                    ">\n" +
                    "    <g id=\"layer1\">\n" +
            "        <path" +
            " id=\"path2789\"\n" +
            "              d=\"m 6.1471992,27.52876 49.5784978,0.133636 0.133633,95.281584 h 46.5049 l -0.13364,-95.147951 h 49.84577 l 0.13363,95.281591 45.56946,0.4009 -0.26727,76.43908 -45.16855,0 v 70.96006 l -51.04848,1.20272 0.13363,-72.02914 H 6.1471992 Z\"\n" +
            "              style=\"fill:none;stroke:#000000;stroke-width:1.565;stroke-linecap:butt;stroke-linejoin:miter;stroke-miterlimit:4;stroke-dasharray:none;stroke-dashoffset:0;stroke-opacity:1\"/>\n" +
            "        <ellipse cx=\"26.103981\" cy=\"44.317707\" rx=\"1\" ry=\"1\" id=\"path2793\" class=\"column\"/>\n" +
            "        <ellipse id=\"path2793-9\" ry=\"1\" rx=\"1\" cy=\"73.185638\" cx=\"26.03311\" class=\"column\"/>\n" +
            "        <ellipse id=\"path2793-6\" ry=\"1\" rx=\"1\" cy=\"102.10082\" cx=\"26.080357\" class=\"column\"/>\n" +
            "        <ellipse id=\"path2793-8\" ry=\"1\" rx=\"1\" cy=\"130.87425\" cx=\"26.127604\" class=\"column\"/>\n" +
            "        <ellipse id=\"path2793-4\" ry=\"1\" rx=\"1\" cy=\"102.05357\" cx=\"122.51153\" class=\"column\"/>\n" +
            "        <ellipse id=\"path2793-82\" ry=\"1\" rx=\"1\" cy=\"123.7792\" cx=\"122.44286\" class=\"column\"/>\n" +
            "        <ellipse id=\"path2793-5\" ry=\"1\" rx=\"1\" cy=\"198.343\" cx=\"122.51154\" class=\"column\"/>\n" +
            "        <ellipse id=\"path2793-81\" ry=\"1\" rx=\"1\" cy=\"222.39175\" cx=\"122.51154\" class=\"column\"/>\n" +
            "        \n" +
            "        " + WORKSTATIONS_PLACEHOLDER + "\n" +
            "        \n" +
            "        <rect y=\"29.055113\" x=\"35.041462\" height=\"11.503038\" width=\"19.347887\" id=\"rect4488\"\n" +
            "              class=\"restricted\"/>\n" +
            "        <rect id=\"rect4490\" width=\"18.281523\" height=\"10.944996\" x=\"7.4989252\" y=\"70.543571\"\n" +
            "              class=\"restricted\"/>\n" +
                    "        <rect id=\"rect4492\" width=\"17.781469\" height=\"14.401464\" x=\"132.88309\" y=\"29.04755\" \n" +
                    "              class=\"restricted\"/>\n" +
                    "        <rect y=\"124.0565\" x=\"132.23834\" height=\"17.984049\" width=\"21.831776\" id=\"rect4494\" \n" +
                    "              class=\"restricted\"/>\n" +
            "        <rect id=\"rect4496\" width=\"24.564068\" height=\"51.085953\" x=\"7.2851453\" y=\"147.73283\"\n" +
            "              class=\"restricted\"/>\n" +
            "        <rect y=\"124.27596\" x=\"31.555872\" height=\"14.370269\" width=\"15.629634\" id=\"rect4498\"\n" +
                    "              class=\"restricted\"/>\n" +
                    "        <rect id=\"rect4500\" width=\"60.639816\" height=\"24.512148\" x=\"31.9282\" y=\"147.81181\" \n" +
                    "              class=\"restricted\"/>\n" +
                    "        <rect y=\"147.84087\" x=\"100.46545\" height=\"24.454029\" width=\"91.197769\" id=\"rect4502\"\n" +
            "              class=\"restricted\"/>\n" +
            "        <rect id=\"rect4504\" width=\"11.672786\" height=\"14.250654\" x=\"139.19159\" y=\"177.81427\"\n" +
            "              class=\"restricted\"/>\n" +
            "        <rect y=\"192.06493\" x=\"130.05791\" height=\"15.656054\" width=\"20.806471\" id=\"rect4506\"\n" +
            "              class=\"restricted\"/>\n" +
            "        <rect id=\"rect4508\" width=\"11.833088\" height=\"12.816936\" x=\"69.216408\" y=\"185.70984\"\n" +
            "              class=\"restricted\"/>\n" +
            "        <rect y=\"186.01071\" x=\"31.858076\" height=\"12.799209\" width=\"17.485004\" id=\"rect4510\"\n" +
            "              class=\"restricted\"/>\n" +
                    "        <rect id=\"rect4512\" width=\"18.84013\" height=\"11.968594\" x=\"103.67917\" y=\"71.528969\" \n" +
                    "              class=\"restricted\"/>\n" +
            "        <rect y=\"124.27596\" x=\"47.185505\" height=\"17.221645\" width=\"69.833839\" id=\"rect4514\"\n" +
            "              class=\"restricted\"/>\n" +
            "    </g>\n" +
            "</svg>";


    //language=SVG
    private static final String TEMPLATE_2D_WORKSTATION =
            "<g transform=\"scale(0.25, 0.25)\">\n" +
                    "    " +
                    "<g\n" +
                    "            class=\"workstation\"\n" +
                    "            transform=\"" + WORKSTATION_TRANSFORMATION_PLACEHOLDER +  "\"\n" +
                    "            id=\"" + WORKSTATION_ID_PLACEHOLDER + "\">\n" +
                    "        <rect\n" +
                    "                width=\"20\"\n" +
                    "                height=\"12\"\n" +
                    "                class=\"workplace " + WORKSTATION_CLASS_PLACEHOLDER + "\"\n" +
                    "                id=\"" + WORKSTATION_ID_PLACEHOLDER + "rect\"\n" +
                    "                fill=\"" + WORKSTATION_FILL_PLACEHOLDER + "\"\n" +
                    "                x=\"-10\"\n" +
                    "                y=\"5\"/>\n" +
                    "        <path\n" +
                    "                style=\"'fill:#000000;fill-opacity:0.345382;stroke:#000000;stroke-width:0.3;stroke-miterlimit:4;stroke-dasharray:none;stroke-dashoffset:0;stroke-opacity:1'\"\n" +
                    "                id=\"" + WORKSTATION_ID_PLACEHOLDER + "chair\"\n" +
                    "                d=\"M0 2 L-2.5 -2 L2.5 -2 Z\"/>\n" +
                    "    </g>\n" +
                    "\n" +
                    "</g>";
    //language=SVG
    private static final String TEMPLATE_3D_PLAN =
            "        <svg\n" +
                    "                xmlns=\"http://www.w3.org/2000/svg\"\n" +
                    "                id=\"svg8\"\n" +
                    "                viewBox=\"-40 -10 240 180\">\n" +
                    "            <defs>\n" +
                    "                <linearGradient id=\"grad1\" x1=\"0%\" y1=\"0%\" x2=\"100%\" y2=\"0%\">\n" +
                    "                    <stop offset=\"0%\" style=\"stop-color:#636363;stop-opacity:1\" />\n" +
                    "                    <stop offset=\"100%\" style=\"stop-color:#939393;stop-opacity:1\" />\n" +
                    "                </linearGradient>\n" +
                    "            </defs>\n" +
                    "            <g id=\"layer1\"\n" +
                    "               transform=\"translate(150, 80) matrix(0.707 0.409 -0.707 0.409 0 -0.816) translate(-150, -80)\">\n" +
                    "                <path\n" +
                    "                        id=\"path2789\"\n" +
                    "                        d=\"m 6.1471992,27.52876 49.5784978,0.133636 0.133633,95.281584 h 46.5049 l -0.13364,-95.147951 h 49.84577 l 0.13363,95.281591 45.56946,0.4009 -0.26727,76.43908 -45.16855,0 v 70.96006 l -51.04848,1.20272 0.13363,-72.02914 H 6.1471992 Z\"\n" +
                    "                        class=\"building\"/>\n" +
                    "        \n" +
                    "        " + WORKSTATIONS_PLACEHOLDER + "\n" +
                    "        \n" +                    "\n" +
                    "                \n" +
                    "                \n" +
                    "                <rect y=\"29.055113\" x=\"35.041462\" height=\"11.503038\" width=\"19.347887\" id=\"rect4488\"\n" +
                    "                      class=\"restricted\"/>\n" +
                    "                <rect id=\"rect4490\" width=\"18.281523\" height=\"10.944996\" x=\"7.4989252\" y=\"70.543571\"\n" +
                    "                      class=\"restricted\"/>\n" +
                    "                <rect id=\"rect4492\" width=\"17.781469\" height=\"14.401464\" x=\"132.88309\" y=\"29.04755\" class=\"restricted\"/>\n" +
                    "                <rect y=\"124.0565\" x=\"132.23834\" height=\"17.984049\" width=\"21.831776\" id=\"rect4494\" class=\"restricted\"/>\n" +
                    "                <rect id=\"rect4496\" width=\"24.564068\" height=\"51.085953\" x=\"7.2851453\" y=\"147.73283\"\n" +
                    "                      class=\"restricted\"/>\n" +
                    "                <rect y=\"124.27596\" x=\"31.555872\" height=\"14.370269\" width=\"15.629634\" id=\"rect4498\"\n" +
                    "                      class=\"restricted\"/>\n" +
                    "                <rect id=\"rect4500\" width=\"60.639816\" height=\"24.512148\" x=\"31.9282\" y=\"147.81181\" class=\"restricted\"/>\n" +
                    "                <rect y=\"147.84087\" x=\"100.46545\" height=\"24.454029\" width=\"91.197769\" id=\"rect4502\"\n" +
                    "                      class=\"restricted\"/>\n" +
                    "                <rect id=\"rect4504\" width=\"11.672786\" height=\"14.250654\" x=\"139.19159\" y=\"177.81427\"\n" +
                    "                      class=\"restricted\"/>\n" +
                    "                <rect y=\"192.06493\" x=\"130.05791\" height=\"15.656054\" width=\"20.806471\" id=\"rect4506\"\n" +
                    "                      class=\"restricted\"/>\n" +
                    "                <rect id=\"rect4508\" width=\"11.833088\" height=\"12.816936\" x=\"69.216408\" y=\"185.70984\"\n" +
                    "                      class=\"restricted\"/>\n" +
                    "                <rect y=\"186.01071\" x=\"31.858076\" height=\"12.799209\" width=\"17.485004\" id=\"rect4510\"\n" +
                    "                      class=\"restricted\"/>\n" +
                    "                <rect id=\"rect4512\" width=\"18.84013\" height=\"11.968594\" x=\"103.67917\" y=\"71.528969\" class=\"restricted\"/>\n" +
                    "                <rect y=\"124.27596\" x=\"47.185505\" height=\"17.221645\" width=\"69.833839\" id=\"rect4514\"\n" +
                    "                      class=\"restricted\"/>\n" +
                    "            </g>\n" +
                    "            <g id=\"layer2\">\n" +
                    "                <g transform=\"translate(86.8, 8.4), scale(0.03,0.1)\">\n" +
                    "                    <path style=\"fill:url(#grad1);\"\n" +
                    "                          d=\"M29,8C13.536,8,1,6.209,1,4v50c0,2.209,12.536,4,28,4s28-1.791,28-4V4C57,6.209,44.464,8,29,8z\"/>\n" +
                    "                    <ellipse style=\"fill:#939393;\" cx=\"29\" cy=\"4\" rx=\"28\" ry=\"4\"/>\n" +
                    "                    <ellipse style=\"fill:url(#grad1);\" cx=\"29\" cy=\"54\" rx=\"28\" ry=\"4\"/>\n" +
                    "                </g><g transform=\"translate(66.4, 20.4), scale(0.03,0.1)\">\n" +
                    "                    <path style=\"fill:url(#grad1);\"\n" +
                    "                          d=\"M29,8C13.536,8,1,6.209,1,4v50c0,2.209,12.536,4,28,4s28-1.791,28-4V4C57,6.209,44.464,8,29,8z\"/>\n" +
                    "                    <ellipse style=\"fill:#939393;\" cx=\"29\" cy=\"4\" rx=\"28\" ry=\"4\"/>\n" +
                    "                    <ellipse style=\"fill:url(#grad1);\" cx=\"29\" cy=\"54\" rx=\"28\" ry=\"4\"/>\n" +
                    "                </g><g transform=\"translate(46.0, 32.4), scale(0.03,0.1)\">\n" +
                    "                    <path style=\"fill:url(#grad1);\"\n" +
                    "                          d=\"M29,8C13.536,8,1,6.209,1,4v50c0,2.209,12.536,4,28,4s28-1.791,28-4V4C57,6.209,44.464,8,29,8z\"/>\n" +
                    "                    <ellipse style=\"fill:#939393;\" cx=\"29\" cy=\"4\" rx=\"28\" ry=\"4\"/>\n" +
                    "                    <ellipse style=\"fill:url(#grad1);\" cx=\"29\" cy=\"54\" rx=\"28\" ry=\"4\"/>\n" +
                    "                </g><g transform=\"translate(25.6, 44.4), scale(0.03,0.1)\">\n" +
                    "                    <path style=\"fill:url(#grad1);\"\n" +
                    "                          d=\"M29,8C13.536,8,1,6.209,1,4v50c0,2.209,12.536,4,28,4s28-1.791,28-4V4C57,6.209,44.464,8,29,8z\"/>\n" +
                    "                    <ellipse style=\"fill:#939393;\" cx=\"29\" cy=\"4\" rx=\"28\" ry=\"4\"/>\n" +
                    "                    <ellipse style=\"fill:url(#grad1);\" cx=\"29\" cy=\"54\" rx=\"28\" ry=\"4\"/>\n" +
                    "                </g>\n" +
                    "                <g transform=\"translate(155.3, 47.8), scale(0.03,0.1)\">\n" +
                    "                    <path style=\"fill:url(#grad1);\"\n" +
                    "                          d=\"M29,8C13.536,8,1,6.209,1,4v50c0,2.209,12.536,4,28,4s28-1.791,28-4V4C57,6.209,44.464,8,29,8z\"/>\n" +
                    "                    <ellipse style=\"fill:#939393;\" cx=\"29\" cy=\"4\" rx=\"28\" ry=\"4\"/>\n" +
                    "                    <ellipse style=\"fill:url(#grad1);\" cx=\"29\" cy=\"54\" rx=\"28\" ry=\"4\"/>\n" +
                    "                </g><g transform=\"translate(134.9, 59.8), scale(0.03,0.1)\">\n" +
                    "                    <path style=\"fill:url(#grad1);\"\n" +
                    "                          d=\"M29,8C13.536,8,1,6.209,1,4v50c0,2.209,12.536,4,28,4s28-1.791,28-4V4C57,6.209,44.464,8,29,8z\"/>\n" +
                    "                    <ellipse style=\"fill:#939393;\" cx=\"29\" cy=\"4\" rx=\"28\" ry=\"4\"/>\n" +
                    "                    <ellipse style=\"fill:url(#grad1);\" cx=\"29\" cy=\"54\" rx=\"28\" ry=\"4\"/>\n" +
                    "                </g><g transform=\"translate(114.5, 71.8), scale(0.03,0.1)\">\n" +
                    "                    <path style=\"fill:url(#grad1);\"\n" +
                    "                          d=\"M29,8C13.536,8,1,6.209,1,4v50c0,2.209,12.536,4,28,4s28-1.791,28-4V4C57,6.209,44.464,8,29,8z\"/>\n" +
                    "                    <ellipse style=\"fill:#939393;\" cx=\"29\" cy=\"4\" rx=\"28\" ry=\"4\"/>\n" +
                    "                    <ellipse style=\"fill:url(#grad1);\" cx=\"29\" cy=\"54\" rx=\"28\" ry=\"4\"/>\n" +
                    "                </g>\n" +
                    "                <g transform=\"translate(46.0, 111.5), scale(0.03,0.1)\">\n" +
                    "                    <path style=\"fill:url(#grad1);\"\n" +
                    "                          d=\"M29,8C13.536,8,1,6.209,1,4v50c0,2.209,12.536,4,28,4s28-1.791,28-4V4C57,6.209,44.464,8,29,8z\"/>\n" +
                    "                    <ellipse style=\"fill:#939393;\" cx=\"29\" cy=\"4\" rx=\"28\" ry=\"4\"/>\n" +
                    "                    <ellipse style=\"fill:url(#grad1);\" cx=\"29\" cy=\"54\" rx=\"28\" ry=\"4\"/>\n" +
                    "                </g><g transform=\"translate(28.7, 121.3), scale(0.03,0.1)\">\n" +
                    "                    <path style=\"fill:url(#grad1);\"\n" +
                    "                          d=\"M29,8C13.536,8,1,6.209,1,4v50c0,2.209,12.536,4,28,4s28-1.791,28-4V4C57,6.209,44.464,8,29,8z\"/>\n" +
                    "                    <ellipse style=\"fill:#939393;\" cx=\"29\" cy=\"4\" rx=\"28\" ry=\"4\"/>\n" +
                    "                    <ellipse style=\"fill:url(#grad1);\" cx=\"29\" cy=\"54\" rx=\"28\" ry=\"4\"/>\n" +
                    "                </g><g transform=\"translate(11.4, 131.1), scale(0.03,0.1)\">\n" +
                    "                    <path style=\"fill:url(#grad1);\"\n" +
                    "                          d=\"M29,8C13.536,8,1,6.209,1,4v50c0,2.209,12.536,4,28,4s28-1.791,28-4V4C57,6.209,44.464,8,29,8z\"/>\n" +
                    "                    <ellipse style=\"fill:#939393;\" cx=\"29\" cy=\"4\" rx=\"28\" ry=\"4\"/>\n" +
                    "                    <ellipse style=\"fill:url(#grad1);\" cx=\"29\" cy=\"54\" rx=\"28\" ry=\"4\"/>\n" +
                    "                </g>\n" +
                    "            </g>\n" +
                    "        </svg>";

    //language=SVG
    private static final String TEMPLATE_3D_WORKSTATION =
            "<g transform=\"scale(0.25, 0.25)\">\n" +
                    "    " +
                    "<g\n" +
                    "            class=\"workstation\"\n" +
                    "            id=\"" + WORKSTATION_ID_PLACEHOLDER + "\">\n" +
                    "        <rect\n" +
                    "                width=\"20\"\n" +
                    "                height=\"12\"\n" +
                    "                class=\"workplace-floor\"\n" +
                    "                transform=\"" + WORKSTATION_TRANSFORMATION_PLACEHOLDER +  "\"\n" +
                    "                x=\"-10\"\n" +
                    "                y=\"5\"/>\n" +
                    "        <rect\n" +
                    "                width=\"20\"\n" +
                    "                height=\"12\"\n" +
                    "                class=\"workplace " + WORKSTATION_CLASS_PLACEHOLDER + "\"\n" +
                    "                transform=\"translate(-5, -5), " + WORKSTATION_TRANSFORMATION_PLACEHOLDER +  "\"\n" +
                    "                id=\"" + WORKSTATION_ID_PLACEHOLDER + "rect\"\n" +
                    "                fill=\"" + WORKSTATION_FILL_PLACEHOLDER + "\"\n" +
                    "                x=\"-10\"\n" +
                    "                y=\"5\"/>\n" +
                    "        <path\n" +
                    "                style=\"'fill:#000000;fill-opacity:0.345382;stroke:#000000;stroke-width:0.3;stroke-miterlimit:4;stroke-dasharray:none;stroke-dashoffset:0;stroke-opacity:1'\"\n" +
                    "                id=\"" + WORKSTATION_ID_PLACEHOLDER + "chair\"\n" +
                    "                transform=\"" + WORKSTATION_TRANSFORMATION_PLACEHOLDER +  "\"\n" +
                    "                d=\"M0 2 L-2.5 -2 L2.5 -2 Z\"/>\n" +
                    "    </g>\n" +
                    "\n" +
                    "</g>";

    @Override
    public String createAdmin2DSVG(List<Workstation> workstations, List<Workstation> occupiable) {
        StringBuilder stationsTemplate = new StringBuilder();
        for (Workstation station : workstations) {
            stationsTemplate.append(fillOutAdminTemplates(TEMPLATE_2D_WORKSTATION, station, occupiable)).append('\n');
        }

        return TEMPLATE_2D_PLAN.replace(WORKSTATIONS_PLACEHOLDER, stationsTemplate.toString());
    }

    @Override
    public String createAdmin3DSVG(List<Workstation> workstations, List<Workstation> occupiable) {
        StringBuilder stationsTemplate = new StringBuilder();
        for (Workstation station : workstations) {
            stationsTemplate.append(fillOutAdminTemplates(TEMPLATE_3D_WORKSTATION, station, occupiable)).append('\n');
        }

        return TEMPLATE_3D_PLAN.replace(WORKSTATIONS_PLACEHOLDER, stationsTemplate.toString());
    }

    @Override
    public String createWorker2DSVG(List<Workstation> workstations, Workstation chosen) {
        StringBuilder stationsTemplate = new StringBuilder();
        for (Workstation station : workstations) {
            stationsTemplate.append(fillOutWorkerTemplate(TEMPLATE_2D_WORKSTATION, station, chosen)).append('\n');
        }
        return TEMPLATE_2D_PLAN.replace(WORKSTATIONS_PLACEHOLDER, stationsTemplate.toString());
    }

    private String fillOutAdminTemplates(String template, Workstation station, List<Workstation> occupiable) {
        template = template.replace(WORKSTATION_ID_PLACEHOLDER, station.getId());
        template = template.replace(WORKSTATION_TRANSFORMATION_PLACEHOLDER, getTransform(station));
        template = template.replace(WORKSTATION_CLASS_PLACEHOLDER, getAdminClass(station, occupiable));
        template = template.replace(WORKSTATION_FILL_PLACEHOLDER, getAdminFill(station, occupiable));
        return template;
    }

    private String fillOutWorkerTemplate(String template, Workstation station, Workstation chosen) {
        template = template.replace(WORKSTATION_ID_PLACEHOLDER, station.getId());
        template = template.replace(WORKSTATION_TRANSFORMATION_PLACEHOLDER, getTransform(station));
        template = template.replace(WORKSTATION_CLASS_PLACEHOLDER, getWorkerClass(station, chosen));
        template = template.replace(WORKSTATION_FILL_PLACEHOLDER, getWorkerFill(station, chosen));
        return template;
    }

    private String getAdminClass(Workstation station, List<Workstation> occupiable) {
        if (!station.isEnabled())
            return "ws-disabled";
        if (occupiable.stream().anyMatch(occupiableStation -> Objects.equals(station.getId(), occupiableStation.getId())))
            return "ws-occupiable";
        if (station.getOccupier() == null) {
            return "ws-infectious";
        }
        if (station.getOccupier().getStatus() == WorkerStatus.InOffice)
            return "ws-occupied";
        return "ws-will-be-occupied";
    }

    private String getAdminFill(Workstation station, List<Workstation> occupiable) {
        if (!station.isEnabled())
            return "grey";
        if (occupiable.stream().anyMatch(occupiableStation -> Objects.equals(station.getId(), occupiableStation.getId())))
            return "green";
        if (station.getOccupier() == null) {
            return "orange";
        }
        if (station.getOccupier().getStatus() == WorkerStatus.InOffice)
            return "red";
        return "yellow";
    }

    private String getWorkerClass(Workstation station, Workstation chosen) {
        return Objects.equals(station.getId(), chosen.getId()) ? "ws-occupiable" : "ws-disabled";
    }

    private String getWorkerFill(Workstation station, Workstation chosen) {
        return Objects.equals(station.getId(), chosen.getId()) ? "green" : "grey";
    }

    private String getTransform(Workstation station) {
        double x = station.getX();
        double y = station.getY();
        switch (station.getOrientation()) {
            case DOWN:
                return String.format(Locale.ROOT, TRANSFORMATION_FORMULA, 1, 0, 0, 1, x, y);
            case UP:
                return String.format(Locale.ROOT, TRANSFORMATION_FORMULA, -1, 0, 0, -1, x, y);
            case LEFT:
                return String.format(Locale.ROOT, TRANSFORMATION_FORMULA, 0, -1, 1, 0, x, y);
            case RIGHT:
                return String.format(Locale.ROOT, TRANSFORMATION_FORMULA, 0, 1, -1, 0, x, y);
        }
        return "";
    }

}
