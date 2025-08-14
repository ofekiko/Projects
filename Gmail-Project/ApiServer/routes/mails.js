const express = require('express')
const router = express.Router()
const controller = require('../controllers/mails')

router.route('/')
        .get(controller.get50LatestMails) // GET returns the last 50 mails of the user in '/' route
        .post(controller.createMail) // POST creates a new mail in the '/' route
router.route('/:id')
        .get(controller.getMailById) // GET return a mail with the id provided in the '/:id' route
        .delete(controller.deleteMail) // DELETE deletes a mail with the id provided in the '/:id' route
        .patch(controller.updateMail) // PATCH updates a mail with the id provided in the '/:id' route
router.route('/box/:box')
        .get(controller.getMailsInBox) // GET return all the mails of the user in '/:label' route
router.route('/search/:query')
        .get(controller.searchMail) // GET return an array of mails that contain the query provided in the '/search/:query' route
router.route('/search/:query/:box')
        .get(controller.searchMailInBox); // GET return an array of mails that contain the query and label provided in the '/search/:query/:box' route

module.exports = router