package com.tictactoe;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Field field = extractField(session);
        int index = getSelectedIndex(req);
        Sign sign = field.getField().get(index);

        if(Sign.EMPTY != sign) {
            getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        field.getField().put(index, Sign.CROSS);
        if(checkWin(resp, session, field)) return;

        int emptyFieldIndex = field.getEmptyFieldIndex();

        if(emptyFieldIndex >= 0) {
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);
            if(checkWin(resp, session, field)) return;
        } else {
            session.setAttribute("draw", true);
            List<Sign> data = field.getFieldData();
            session.setAttribute("data", data);
            resp.sendRedirect("/index.jsp");
            return;
        }

        List<Sign> data = field.getFieldData();

        session.setAttribute("data", data);
        session.setAttribute("field", field);
        resp.sendRedirect("/index.jsp");
    }

    private int getSelectedIndex(HttpServletRequest req) {
        String click = req.getParameter("click");
        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        return isNumeric ? Integer.parseInt(click) : 0;
    }

    private Field extractField(HttpSession session) {
        Object fieldAttr = session.getAttribute("field");
        if(fieldAttr.getClass() != Field.class) {
            session.invalidate();
            throw new RuntimeException("Session not correct");
        }
        return (Field) fieldAttr;
    }

    private boolean checkWin(HttpServletResponse resp, HttpSession session, Field field) throws IOException {
        Sign winner = field.checkWin();
        if(Sign.CROSS == winner || Sign.NOUGHT == winner) {
            session.setAttribute("winner", winner);
            List<Sign> data = field.getFieldData();
            session.setAttribute("data", data);
            resp.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }
}
