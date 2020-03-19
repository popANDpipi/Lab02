﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Rendering;
using Microsoft.EntityFrameworkCore;
using SurveyServer.Data;
using SurveyServer.Models;

namespace SurveyServer.Controllers
{
    public class AnswersController : Controller
    {
        private readonly ApplicationDbContext _context;

        public AnswersController(ApplicationDbContext context)
        {
            _context = context;
        }

        // GET: Answers
        public async Task<IActionResult> Index()
        {
            var applicationDbContext = _context.Answer.Include(a => a.Response);
            return View(await applicationDbContext.ToListAsync());
        }

        // GET: Answers/Details/5
        public async Task<IActionResult> Details(string id)
        {
            if (id == null)
            {
                return NotFound();
            }

            var answer = await _context.Answer
                .Include(a => a.Response)
                .FirstOrDefaultAsync(m => m.AnswerKey == id);
            if (answer == null)
            {
                return NotFound();
            }

            return View(answer);
        }

        //// GET: Answers/Create
        //public IActionResult Create()
        //{
        //    ViewData["RefResponseID"] = new SelectList(_context.Response, "ResponseID", "ResponseID");
        //    return View();
        //}

        //// POST: Answers/Create
        //// To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        //// more details see http://go.microsoft.com/fwlink/?LinkId=317598.
        //[HttpPost]
        //[ValidateAntiForgeryToken]
        //public async Task<IActionResult> Create([Bind("AnswerKey,QuestionNum,Content,RefResponseID")] Answer answer)
        //{
        //    if (ModelState.IsValid)
        //    {
        //        _context.Add(answer);
        //        await _context.SaveChangesAsync();
        //        return RedirectToAction(nameof(Index));
        //    }
        //    ViewData["RefResponseID"] = new SelectList(_context.Response, "ResponseID", "ResponseID", answer.RefResponseID);
        //    return View(answer);
        //}

        //// GET: Answers/Edit/5
        //public async Task<IActionResult> Edit(string id)
        //{
        //    if (id == null)
        //    {
        //        return NotFound();
        //    }

        //    var answer = await _context.Answer.FindAsync(id);
        //    if (answer == null)
        //    {
        //        return NotFound();
        //    }
        //    ViewData["RefResponseID"] = new SelectList(_context.Response, "ResponseID", "ResponseID", answer.RefResponseID);
        //    return View(answer);
        //}

        //// POST: Answers/Edit/5
        //// To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        //// more details see http://go.microsoft.com/fwlink/?LinkId=317598.
        //[HttpPost]
        //[ValidateAntiForgeryToken]
        //public async Task<IActionResult> Edit(string id, [Bind("AnswerKey,QuestionNum,Content,RefResponseID")] Answer answer)
        //{
        //    if (id != answer.AnswerKey)
        //    {
        //        return NotFound();
        //    }

        //    if (ModelState.IsValid)
        //    {
        //        try
        //        {
        //            _context.Update(answer);
        //            await _context.SaveChangesAsync();
        //        }
        //        catch (DbUpdateConcurrencyException)
        //        {
        //            if (!AnswerExists(answer.AnswerKey))
        //            {
        //                return NotFound();
        //            }
        //            else
        //            {
        //                throw;
        //            }
        //        }
        //        return RedirectToAction(nameof(Index));
        //    }
        //    ViewData["RefResponseID"] = new SelectList(_context.Response, "ResponseID", "ResponseID", answer.RefResponseID);
        //    return View(answer);
        //}

        // GET: Answers/Delete/5
        public async Task<IActionResult> Delete(string id)
        {
            if (id == null)
            {
                return NotFound();
            }

            var answer = await _context.Answer
                .Include(a => a.Response)
                .FirstOrDefaultAsync(m => m.AnswerKey == id);
            if (answer == null)
            {
                return NotFound();
            }

            return View(answer);
        }

        // POST: Answers/Delete/5
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> DeleteConfirmed(string id)
        {
            var answer = await _context.Answer.FindAsync(id);
            _context.Answer.Remove(answer);
            await _context.SaveChangesAsync();
            return RedirectToAction(nameof(Index));
        }

        private bool AnswerExists(string id)
        {
            return _context.Answer.Any(e => e.AnswerKey == id);
        }
    }
}
