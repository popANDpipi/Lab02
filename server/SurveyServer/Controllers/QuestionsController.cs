using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Rendering;
using Microsoft.EntityFrameworkCore;
using SurveyServer.Data;
using SurveyServer.Models;
using SurveyServer.Scripts;

namespace SurveyServer.Controllers
{
    public class QuestionsController : Controller
    {
        private readonly ApplicationDbContext _context;

        public QuestionsController(ApplicationDbContext context)
        {
            _context = context;
        }

        // GET: Questions
        public async Task<IActionResult> Index()
        {
            var applicationDbContext = _context.Question.Include(q => q.Survey).OrderBy(m=>m.QuestionNum);
            return View(await applicationDbContext.ToListAsync());
        }

        // GET: Questions/Details/5
        public async Task<IActionResult> Details(string id)
        {
            if (id == null)
            {
                return NotFound();
            }
            
            var question = await _context.Question
                .Include(q => q.Survey)
                .FirstOrDefaultAsync(m => m.QuestionKey == id);
            if (question == null)
            {
                return NotFound();
            }

            return View(question);
        }

        // GET: Questions/Create
        public IActionResult Create(string id)
        {
            var result = _context.Question.Include(q => q.Survey).Where(m => m.RefSurveyID == id);
            if (result == null)
            {
                return View();
            }
            int max = 0;
            foreach (Question question in result)
            {
                if (question.QuestionNum> max)
                {
                    max = question.QuestionNum;
                }
            }

            ViewData["QuestionNum"] = max + 1;
            ViewData["QuestionKey"] = RandomStringBuilder.Create();
            ViewData["RefSurveyID"] = id;
            return View();
        }

        // POST: Questions/Create
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see http://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Create([Bind("QuestionKey,QuestionNum,QuestionDescription,RefSurveyID,QuestionType,Options")] Question question)
        {
            if (ModelState.IsValid)
            {
                _context.Add(question);
                await _context.SaveChangesAsync();
                return RedirectToAction(nameof(Index));
            }
            
            ViewData["RefSurveyID"] = question.RefSurveyID;
            return RedirectToAction("Details", "Surveys", question.RefSurveyID);
        }

        // GET: Questions/Edit/5
        public async Task<IActionResult> Edit(string id)
        {
            if (id == null)
            {
                return NotFound();
            }

            var question = await _context.Question.FindAsync(id);
            if (question == null)
            {
                return NotFound();
            }
            ViewData["RefSurveyID"] = question.RefSurveyID;
            return View(question);
        }

        // POST: Questions/Edit/5
        // To protect from overposting attacks, please enable the specific properties you want to bind to, for 
        // more details see http://go.microsoft.com/fwlink/?LinkId=317598.
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Edit(string id, [Bind("QuestionKey,QuestionNum,QuestionDescription,RefSurveyID,QuestionType,Options")] Question question)
        {
            if (id != question.QuestionKey)
            {
                return NotFound();
            }

            if (ModelState.IsValid)
            {
                try
                {
                    _context.Update(question);
                    await _context.SaveChangesAsync();
                }
                catch (DbUpdateConcurrencyException)
                {
                    if (!QuestionExists(question.QuestionKey))
                    {
                        return NotFound();
                    }
                    else
                    {
                        throw;
                    }
                }
                return RedirectToAction(nameof(Index));
            }
            ViewData["RefSurveyID"] = question.RefSurveyID;
            return View(question);
        }

        // GET: Questions/Delete/5
        public async Task<IActionResult> Delete(string id)
        {
            if (id == null)
            {
                return NotFound();
            }

            var question = await _context.Question
                .Include(q => q.Survey)
                .FirstOrDefaultAsync(m => m.QuestionKey == id);
            if (question == null)
            {
                return NotFound();
            }

            return View(question);
        }

        // POST: Questions/Delete/5
        [HttpPost, ActionName("Delete")]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> DeleteConfirmed(string id)
        {
            var question = await _context.Question.FindAsync(id);
            _context.Question.Remove(question);
            await _context.SaveChangesAsync();
            return RedirectToAction(nameof(Index));
        }

        private bool QuestionExists(string id)
        {
            return _context.Question.Any(e => e.QuestionKey == id);
        }
    }
}
